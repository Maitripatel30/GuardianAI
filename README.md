# 🛡️ GuardianAI

**GuardianAI** is an Android-based personal safety and emergency assistance application designed to help users quickly respond to emergency situations. The application provides features such as **SOS alerts, emergency contacts, live location sharing, emergency calling, safety check timers, notifications, and emergency history**.

The application is developed using **Kotlin and Android Studio** with **Firebase Authentication and Cloud Firestore** for secure user authentication and cloud data management.

---

## 📱 Project Overview

In an emergency situation, a person may not have enough time to manually contact family members or friends.

GuardianAI provides a centralized safety system where users can:

* Register and securely log in.
* Add and manage emergency contacts.
* Activate an SOS alert.
* Send emergency SMS messages to saved contacts.
* Share their current location through Google Maps.
* Call emergency contacts directly.
* Start a Safety Check timer.
* Receive notifications for emergency activities.
* View their previous emergency activities through Emergency History.

The main objective of GuardianAI is to provide a **simple, fast, and reliable personal safety solution**.

---

## ✨ Features

### 🔐 User Authentication

* User Registration
* User Login
* Firebase Authentication
* Logout functionality
* User-specific data management

### 👥 Emergency Contacts

Users can add emergency contacts with:

* Contact Name
* Phone Number

The contacts are securely stored in Firebase Cloud Firestore.

### 🚨 SOS Emergency Alert

The SOS feature allows users to quickly activate an emergency alert.

When SOS is activated:

1. The user confirms the emergency action.
2. GuardianAI retrieves saved emergency contacts.
3. The current location is obtained.
4. An emergency SMS is sent to the saved contacts.
5. The emergency activity is stored in Firestore.
6. The user can also call an emergency contact.

Example emergency message:

> 🚨 GUARDIAN AI EMERGENCY!
> SOS has been activated.
> Please check my current location.

---

### 📍 Location Sharing

The Location feature retrieves the user's current location and creates a Google Maps location link.

The location can be shared with emergency contacts through SMS.

Example:

```text
https://maps.google.com/?q=LATITUDE,LONGITUDE
```

Users can also open their current location directly in Google Maps.

---

### 📞 Emergency Calling

Users can call an emergency contact directly from the application.

The application requests the required phone-call permission before placing the call.

---

### 🛡️ Safety Check

The Safety Check feature allows users to set a timer for a specific period.

Available timer options include:

* 5 minutes
* 10 minutes
* 15 minutes
* 30 minutes
* 60 minutes

The user can select **I'm Safe** when they are safe.

If the timer expires without confirmation:

* A missed safety check is recorded.
* The application generates an emergency notification.
* Emergency contacts can be alerted with the user's location.

---

### 🔔 Notifications

GuardianAI maintains emergency-related notifications for the user.

Notifications can include:

* 🚨 SOS Activated
* 📍 Location Shared
* 📞 Emergency Call
* 🛡️ Safety Check Completed
* ⚠️ Safety Check Missed

Android notification channels are used for emergency notifications.

---

### 📊 Emergency History

The Emergency History section provides a chronological record of emergency-related activities.

It displays:

* Activity type
* Activity title
* Description
* Date
* Time

History data is retrieved from Firebase Cloud Firestore and displayed with the newest activities first.

---

## 🏗️ Technology Stack

| Technology                   | Purpose                 |
| ---------------------------- | ----------------------- |
| **Kotlin**                   | Application development |
| **Android Studio**           | Development environment |
| **XML**                      | User interface design   |
| **ConstraintLayout**         | Responsive UI layouts   |
| **Material Components**      | Modern Android UI       |
| **Firebase Authentication**  | User authentication     |
| **Cloud Firestore**          | Cloud database          |
| **Firebase**                 | Backend services        |
| **Fused Location Provider**  | Location detection      |
| **Google Maps**              | Location visualization  |
| **Android SmsManager**       | Emergency SMS           |
| **Android Notification API** | Emergency notifications |
| **RecyclerView**             | Emergency history list  |

---

## 🔥 Firebase Structure

GuardianAI uses Firebase Authentication and Cloud Firestore.

The Firestore structure is organized around individual users:

```text
users
│
├── {userId}
│   │
│   ├── emergencyContacts
│   │   ├── {contactId}
│   │   │   ├── name
│   │   │   └── phone
│   │
│   └── notifications
│       ├── {notificationId}
│       │   ├── type
│       │   ├── title
│       │   ├── description
│       │   ├── time
│       │   └── timestamp
```

This structure keeps emergency contacts and notification history associated with the authenticated user.

---

## 📂 Main Application Components

```text
GuardianAI
│
├── LoginActivity
├── SignupActivity
├── MainActivity
├── EmergencyContactsActivity
├── SafetyCheckActivity
├── NotificationsActivity
├── EmergencyHistoryActivity
├── ProfileActivity
│
├── HistoryItem
├── EmergencyHistoryAdapter
│
└── Firebase
    ├── Authentication
    └── Cloud Firestore
```

---

## 🔑 Required Android Permissions

GuardianAI requires permissions for its emergency functionality.

```xml
<uses-permission android:name="android.permission.CALL_PHONE" />

<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<uses-permission android:name="android.permission.SEND_SMS" />

<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

Permissions are requested at runtime where required by Android.

---

## ⚙️ Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/Maitripatel30/GuardianAI.git
```

Open the project in **Android Studio**.

### 2. Configure Firebase

Create a Firebase project and connect it with the Android application.

Enable:

* Firebase Authentication
* Email/Password Authentication
* Cloud Firestore

Add the Firebase configuration file:

```text
google-services.json
```

Place it inside:

```text
app/
```

> **Security:** Do not upload private credentials, API secrets, or other sensitive configuration files to a public repository.

### 3. Sync Gradle

Open the project in Android Studio and allow Gradle to sync the project dependencies.

### 4. Run the Application

Connect an Android device or start an Android emulator and run the application from Android Studio.

---

## 📱 Application Flow

```text
Start Application
       │
       ▼
   Splash Screen
       │
       ▼
   Login / Signup
       │
       ▼
     Dashboard
       │
       ├───────────────┐
       │               │
       ▼               ▼
 Emergency        Location
 Contacts          Sharing
       │
       ▼
    Add Contacts
       │
       ▼
    Emergency
      Action
       │
       ├── SOS
       ├── Location
       ├── Call
       └── Safety Check
       │
       ▼
 Notifications
       │
       ▼
 Emergency History
```

---

## 🚨 Emergency Workflow

```text
User activates SOS
        │
        ▼
Confirm Emergency
        │
        ▼
Load Emergency Contacts
        │
        ▼
Get Current Location
        │
        ▼
Generate Google Maps Link
        │
        ▼
Send Emergency SMS
        │
        ▼
Save Emergency Activity
        │
        ▼
Show Emergency Notification
```

---

## 🎯 Project Objectives

The major objectives of GuardianAI are:

1. To provide a quick emergency response system.
2. To allow users to store trusted emergency contacts.
3. To automatically share location during emergency situations.
4. To provide direct emergency calling.
5. To provide safety-check monitoring.
6. To maintain emergency activity history.
7. To securely store user-related emergency data using Firebase.
8. To provide a simple and user-friendly Android interface.

---

## 🌟 Advantages

* Fast emergency response
* Simple and user-friendly interface
* Firebase-based authentication
* Cloud-based emergency data
* Emergency location sharing
* Direct calling functionality
* Emergency SMS support
* Safety Check monitoring
* Emergency activity history
* Android notification support

---

## 🔮 Future Enhancements

Possible future improvements include:

* Background location tracking
* Persistent Safety Check timer
* Automatic fall detection
* Voice-activated SOS
* Shake-to-activate SOS
* Wearable device integration
* Real-time location tracking
* Multiple emergency alert channels
* Web-based emergency contact dashboard
* AI-based emergency situation detection

---

## ⚠️ Important Notes

GuardianAI uses Android SMS and calling capabilities. Actual SMS delivery depends on the device, SIM card, mobile network, permissions, and Android system restrictions.

Location services must be enabled for location-based emergency features.

For testing SMS functionality, a physical Android device with an active SIM/mobile network is recommended.

---

## 🧪 Testing

The following major functionalities should be tested:

* [ ] User Signup
* [ ] User Login
* [ ] Logout
* [ ] Add Emergency Contact
* [ ] Delete/Manage Emergency Contact
* [ ] SOS Activation
* [ ] Emergency SMS
* [ ] Location Retrieval
* [ ] Location Sharing
* [ ] Emergency Calling
* [ ] Safety Check Timer
* [ ] Safety Check Completion
* [ ] Missed Safety Check
* [ ] Notifications
* [ ] Emergency History
* [ ] Firebase Data Storage

---

## 👩‍💻 Developer

**Maitri Patel**

---
