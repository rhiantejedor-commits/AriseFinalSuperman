package com.example.arise.data.remote;

import android.content.Context;
import android.util.Log;

import com.example.arise.data.local.HabitEntity;
import com.example.arise.data.local.UserStatsEntity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";
    private static FirebaseManager instance;

    private boolean isFirebaseAvailable = false;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public interface AuthCallback {
        void onSuccess(String uid, String email, String displayName);
        void onError(String errorMessage);
    }

    public interface SyncCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    private FirebaseManager(Context context) {
        try {
            if (!FirebaseApp.getApps(context.getApplicationContext()).isEmpty()) {
                mAuth = FirebaseAuth.getInstance();
                mFirestore = FirebaseFirestore.getInstance();
                isFirebaseAvailable = true;
                Log.d(TAG, "Firebase successfully initialized.");
            } else {
                Log.w(TAG, "FirebaseApp is empty. Operating in offline fallback mode.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Firebase initialization skipped (google-services.json missing/pending): " + e.getMessage());
            isFirebaseAvailable = false;
        }
    }

    public static synchronized FirebaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseManager(context);
        }
        return instance;
    }

    public boolean isFirebaseAvailable() {
        return isFirebaseAvailable && mAuth != null;
    }

    public boolean isLoggedIn() {
        return isFirebaseAvailable() && mAuth.getCurrentUser() != null;
    }

    public FirebaseUser getCurrentUser() {
        if (isFirebaseAvailable()) {
            return mAuth.getCurrentUser();
        }
        return null;
    }

    public void login(String email, String password, AuthCallback callback) {
        if (!isFirebaseAvailable()) {
            String derivedName = extractNameFromEmail(email);
            callback.onSuccess("guest_uid", email, derivedName);
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        String name = user.getDisplayName();
                        if (name != null && !name.trim().isEmpty()) {
                            callback.onSuccess(user.getUid(), user.getEmail(), name.trim());
                        } else if (mFirestore != null) {
                            mFirestore.collection("users").document(user.getUid()).get()
                                    .addOnSuccessListener(doc -> {
                                        String fsName = doc.exists() ? doc.getString("displayName") : null;
                                        if (fsName != null && !fsName.trim().isEmpty()) {
                                            callback.onSuccess(user.getUid(), user.getEmail(), fsName.trim());
                                        } else {
                                            callback.onSuccess(user.getUid(), user.getEmail(), extractNameFromEmail(user.getEmail()));
                                        }
                                    })
                                    .addOnFailureListener(e -> callback.onSuccess(user.getUid(), user.getEmail(), extractNameFromEmail(user.getEmail())));
                        } else {
                            callback.onSuccess(user.getUid(), user.getEmail(), extractNameFromEmail(user.getEmail()));
                        }
                    } else {
                        callback.onError("User account not found");
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getLocalizedMessage()));
    }

    public void register(String email, String password, String displayName, AuthCallback callback) {
        if (!isFirebaseAvailable()) {
            callback.onSuccess("guest_uid", email, displayName);
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        com.google.firebase.auth.UserProfileChangeRequest profileUpdates =
                                new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                        .setDisplayName(displayName)
                                        .build();
                        user.updateProfile(profileUpdates);

                        if (mFirestore != null) {
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("displayName", displayName);
                            userData.put("email", email);
                            mFirestore.collection("users").document(user.getUid())
                                    .set(userData, SetOptions.merge());
                        }

                        callback.onSuccess(user.getUid(), email, displayName);
                    } else {
                        callback.onError("Registration failed");
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getLocalizedMessage()));
    }

    public static String toShortGreetingName(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "User";
        }

        String name = input.trim();

        // 1. If input is an email, extract prefix before '@'
        if (name.contains("@")) {
            name = name.substring(0, name.indexOf("@")).trim();
        }

        // 2. If name contains spaces, dots, underscores, or hyphens, take the first word
        String[] parts = name.split("[\\s._\\-]+");
        if (parts.length > 0 && !parts[0].isEmpty()) {
            name = parts[0];
        }

        // 3. Strip numbers/digits (e.g., "rhian02" -> "rhian", "john123" -> "john")
        name = name.replaceAll("\\d+", "").trim();

        if (name.isEmpty()) {
            return "User";
        }

        // 4. Check for recognized first name prefix if handle is concatenated (e.g. "rhianznisnisan" -> "rhian")
        String lower = name.toLowerCase();
        String[] commonPrefixes = {
                "rhian", "marianne", "john", "mary", "james", "michael", "david", "sarah",
                "chris", "alex", "daniel", "emily", "jessica", "ashley", "amanda", "matthew",
                "joshua", "andrew", "brian", "robert", "william", "joseph", "anthony", "thomas",
                "charles", "christopher", "elizabeth", "jennifer", "linda", "barbara", "patricia",
                "susan", "karen", "nancy", "lisa", "betty", "margaret", "sandra", "kimberly",
                "donna", "michelle", "carol", "melissa", "deborah", "stephanie", "dorothy",
                "rebecca", "sharon", "laura", "cynthia", "kathleen", "amy", "shirley", "angela",
                "helen", "anna", "brenda", "pamela", "nicole", "samantha", "katherine", "emma",
                "virginia", "debra", "rachel", "carolyn", "janet", "catherine", "maria", "heather",
                "diane", "ruth", "julie", "olive", "joyce", "evelyn", "joan", "victoria", "kelly",
                "christina", "lauren", "frances", "martha", "judith", "cherie", "megan", "cheryl",
                "andrea", "hannah", "jacqueline", "ann", "jean", "alice", "kathryn", "gloria",
                "teresa", "doris", "sara", "janice", "jeanette", "julia", "marie", "madison",
                "grace", "judy", "theresa", "beverly", "denise", "amber", "danielle", "abigail",
                "brittany", "rose", "diana", "natalie", "sophia", "alexis", "lori", "kayla", "jane"
        };

        for (String prefix : commonPrefixes) {
            if (lower.startsWith(prefix) && lower.length() > prefix.length()) {
                name = prefix;
                break;
            }
        }

        // 5. Capitalize first letter properly
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    public static String extractNameFromEmail(String email) {
        return toShortGreetingName(email);
    }

    public void syncUserStatsToCloud(UserStatsEntity stats, SyncCallback callback) {
        if (!isLoggedIn() || mFirestore == null) {
            if (callback != null) callback.onSuccess(); // Offline fallback
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();
        Map<String, Object> data = new HashMap<>();
        data.put("displayName", stats.getDisplayName());
        data.put("email", stats.getEmail());
        data.put("currentXp", stats.getCurrentXp());
        data.put("currentLevel", stats.getCurrentLevel());
        data.put("totalDaysCompleted", stats.getTotalDaysCompleted());
        data.put("longestStreak", stats.getLongestStreak());
        data.put("totalMissed", stats.getTotalMissed());

        mFirestore.collection("users").document(uid)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onError(e.getMessage());
                });
    }

    public void syncHabitsToCloud(List<HabitEntity> habits, SyncCallback callback) {
        if (!isLoggedIn() || mFirestore == null) {
            if (callback != null) callback.onSuccess();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();
        for (HabitEntity habit : habits) {
            Map<String, Object> map = new HashMap<>();
            map.put("title", habit.getTitle());
            map.put("category", habit.getCategory());
            map.put("time", habit.getTime());
            map.put("repeatDays", habit.getRepeatDays());
            map.put("isRecurring", habit.isRecurring());
            map.put("xpReward", habit.getXpReward());
            map.put("isCompleted", habit.isCompleted());
            map.put("streakCount", habit.getStreakCount());

            mFirestore.collection("users").document(uid)
                    .collection("habits").document(String.valueOf(habit.getId()))
                    .set(map, SetOptions.merge());
        }

        if (callback != null) callback.onSuccess();
    }

    public void signOut() {
        if (isFirebaseAvailable()) {
            mAuth.signOut();
        }
    }
}
