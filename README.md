# 🛡️ GuardianAI

**GuardianAI** is an Android-based personal safety and emergency assistance application designed to provide quick help during emergency situations.

The application allows users to manage emergency contacts, activate SOS alerts, share their current location, make emergency calls, perform safety checks, receive emergency notifications, and view their emergency history.

---

## 📱 Project Overview

In an emergency situation, a user may not have enough time to manually contact multiple people.

GuardianAI provides multiple safety features in a single Android application so that users can quickly communicate with their trusted emergency contacts.

The application uses **Kotlin, Android Studio, Firebase Authentication, Cloud Firestore, Android Location APIs, SMS services, and Android Notifications**.

---

## ✨ Features

### 🔐 1. User Authentication

* User Signup
* User Login
* Firebase Email/Password Authentication
* Logout
* User-specific data management

---

### 👥 2. Emergency Contacts

Users can add and manage trusted emergency contacts.

Each contact contains:

* Name
* Phone Number

Emergency contacts are stored in Cloud Firestore according to the authenticated user's UID.

---

### 🚨 3. Emergency SOS

The SOS feature provides a quick emergency response mechanism.

When the user activates SOS:

1. The application asks for confirmation.
2. Emergency contacts are retrieved from Firestore.
3. The user's current location is obtained.
4. A Google Maps location link is generated.
5. Emergency SMS is sent to the saved contacts.
6. The SOS activity is stored in Firestore.
7. An emergency notification is generated.
8. The user can call an emergency contact.

Example emergency location link:

```text
https://maps.google.com/?q=LATITUDE,LONGITUDE
```

---

### 📍 4. Location Sharing

The Location feature obtains the user's current location using the **Fused Location Provider**.

The application:

* Gets latitude and longitude.
* Generates a Google Maps link.
* Shares the location with emergency contacts through SMS.
* Stores the location-sharing activity in Emergency History.
* Allows the user to open the location in Google Maps.

---

### 📞 5. Emergency Calling

Users can directly call their emergency contacts from the application.

The application requests the required `CALL_PHONE` permission before making a call.

---

### 🛡️ 6. Safety Check

Safety Check allows users to set a timer and confirm that they are safe.

Available timer durations:

* 5 minutes
* 10 minutes
* 15 minutes
* 30 minutes
* 60 minutes

If the user presses **I'm Safe**, the safety check is completed.

If the timer expires without confirmation:

* A missed safety check is recorded.
* An emergency notification is generated.
* Emergency contacts can be alerted with the user's location.

---

### 🔔 7. Emergency Notifications

GuardianAI maintains notifications for important safety activities.

Notification types include:

* 🚨 SOS Activated
* 📍 Location Shared
* 📞 Emergency Call
* 🛡️ Safety Check Completed
* ⚠️ Safety Check Missed

Notifications are stored in Firestore and displayed inside the application.

---

### 📊 8. Emergency History

Emergency History provides a record of previous safety-related activities.

It displays:

* Activity type
* Activity title
* Description
* Date
* Time

The history is retrieved from Firestore and displayed with the newest activities first.

---

### 👤 9. User Profile

The Profile section provides information about the logged-in user and allows the user to log out of the application.

---

## 🏗️ Technology Stack

| Technology                   | Purpose                         |
| ---------------------------- | ------------------------------- |
| **Kotlin**                   | Android application development |
| **Android Studio**           | Development environment         |
| **XML**                      | User interface                  |
| **ConstraintLayout**         | Responsive UI layouts           |
| **MaterialCardView**         | Modern card-based interface     |
| **Firebase Authentication**  | User authentication             |
| **Cloud Firestore**          | Database                        |
| **Fused Location Provider**  | Current location                |
| **Google Maps**              | Location visualization          |
| **SmsManager**               | Emergency SMS                   |
| **Android Notification API** | Emergency notifications         |
| **RecyclerView**             | Emergency History               |

---

## 🔥 Firebase Database Structure

GuardianAI uses Cloud Firestore to store user-specific emergency data.

```text
users
│
└── {userId}
    │
    ├── emergencyContacts
    │   │
    │   └── {contactId}
    │       ├── name
    │       └── phone
    │
    └── notifications
        │
        └── {notificationId}
            ├── type
            ├── title
            ├── description
            ├── time
            └── timestamp
```

The Firebase Authentication UID is used to associate data with the correct user.

---

## 📂 Application Structure

```text
GuardianAI
│
├── SplashActivity
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

## 🔑 Android Permissions

GuardianAI uses the following permissions for its emergency functionality:

```xml
<uses-permission android:name="android.permission.CALL_PHONE" />

<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<uses-permission android:name="android.permission.SEND_SMS" />

<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

Required permissions are requested at runtime where applicable.

---

## 🔄 Application Workflow

```text
                    GuardianAI
                         │
                         ▼
                  Splash Screen
                         │
                         ▼
                  Login / Signup
                         │
                         ▼
                   Main Dashboard
                         │
          ┌──────────────┼──────────────┐
          │              │              │
          ▼              ▼              ▼
     Emergency       Location       Safety Check
      Contacts        Sharing
          │              │              │
          └──────────────┼──────────────┘
                         │
                         ▼
                Emergency Activities
                         │
             ┌───────────┼───────────┐
             ▼           ▼           ▼
            SOS         Call      Notifications
             │
             ▼
      Emergency History
```

---

## 🚨 SOS Workflow

```text
User presses SOS
       ↓
Confirmation Dialog
       ↓
Load Emergency Contacts
       ↓
Check Permissions
       ↓
Get Current Location
       ↓
Generate Google Maps Link
       ↓
Send Emergency SMS
       ↓
Save SOS Activity
       ↓
Show Emergency Notification
```

---

## 📍 Location Sharing Workflow

```text
User presses Location
        ↓
Load Emergency Contacts
        ↓
Check Location & SMS Permissions
        ↓
Get Current Location
        ↓
Latitude + Longitude
        ↓
Generate Google Maps Link
        ↓
Send Location through SMS
        ↓
Save Location Activity
```

---

## 🎯 Project Objectives

The main objectives of GuardianAI are:

1. To provide a quick emergency response system.
2. To allow users to store trusted emergency contacts.
3. To share the user's current location during emergency situations.
4. To provide direct emergency calling.
5. To provide a Safety Check mechanism.
6. To maintain emergency activity history.
7. To use Firebase for authentication and cloud data storage.
8. To provide a simple and user-friendly safety application.

---

## 🌟 Advantages

* Simple and user-friendly interface
* Quick emergency response
* Emergency SMS support
* Location sharing
* Direct emergency calling
* Firebase-based authentication
* Cloud-based data storage
* Safety Check monitoring
* Emergency notifications
* Emergency History
* User-specific emergency data

---

## ⚠️ Limitations

* SMS functionality depends on the device, SIM card and mobile network.
* Location availability depends on device location services.
* Required Android permissions must be granted by the user.
* The current Safety Check timer is not designed as a persistent background service after the application is completely force-stopped.
* Google Maps availability depends on the device.

---

## 🔮 Future Enhancements

Future versions of GuardianAI could include:

* 🎙️ Voice-activated SOS
* 📳 Shake-to-activate SOS
* 📍 Real-time location tracking
* 🧠 AI-based emergency detection
* 🚶 Fall detection
* ⌚ Smartwatch / wearable integration
* 🔋 Background safety monitoring
* 🌐 Web-based emergency dashboard
* 👨‍👩‍👧 Family tracking and monitoring
* 📞 Automatic emergency service integration

---

## ⚙️ Installation

### 1. Clone the Repository

```bash
git clone https://github.com/Maitripatel30/GuardianAI.git
```

### 2. Open in Android Studio

Open the cloned project using Android Studio.

### 3. Configure Firebase

Create a Firebase project and enable:

* Firebase Authentication
* Email/Password Authentication
* Cloud Firestore

Add your Firebase configuration file:

```text
google-services.json
```

inside:

```text
app/
```

> Do not commit private credentials or sensitive configuration files to a public repository.

### 4. Sync Gradle

Allow Android Studio to sync all Gradle dependencies.

### 5. Run the Application

Connect an Android device or use an Android emulator and run the application.

For SMS and calling features, a physical Android device with an active SIM/mobile network is recommended.

---

## 🧪 Testing Checklist

* [ ] User Signup
* [ ] User Login
* [ ] User Logout
* [ ] Add Emergency Contact
* [ ] Display Emergency Contacts
* [ ] SOS Activation
* [ ] Emergency SMS
* [ ] Location Retrieval
* [ ] Location Sharing
* [ ] Google Maps
* [ ] Emergency Calling
* [ ] Safety Check
* [ ] Safety Check Completion
* [ ] Missed Safety Check
* [ ] Notifications
* [ ] Emergency History
* [ ] Firebase Data Storage

---

## 🤖 AI-Assisted Development

AI tools were used as development assistance during the project.

AI assistance was used for:

* Understanding Android and Kotlin concepts
* Generating initial code suggestions
* Firebase integration guidance
* UI implementation assistance
* Debugging and error analysis
* Improving existing code
* Understanding Android permissions
* Troubleshooting location and SMS functionality

The generated solutions were reviewed, integrated, tested and modified according to the requirements of the GuardianAI application.

---

## 👩‍💻 Developer

### Maitri Patel

GitHub:

https://github.com/Maitripatel30

Project Repository:

https://github.com/Maitripatel30/GuardianAI

---
