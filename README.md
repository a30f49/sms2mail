# sms2mail

An Android application that forwards incoming SMS messages to a specified email address.

## Features

- Forwards all incoming SMS messages to a configured email address.
- Allows for custom SMTP server configuration.
- Runs as a background service to ensure continuous monitoring of new messages.
- Saves your configuration settings locally on the device.

## How to Use

1.  **Open the App:** Launch the sms2mail application on your Android device.
2.  **Configure Settings:**
    - **Sender Email:** Enter the email address from which the forwarded SMS will be sent.
    - **Email Password/App-Specific Password:** Enter the password for the sender's email account. For services like Gmail, you may need to use an "App Password".
    - **Receiver Email:** Enter the email address where you want to receive the SMS messages.
    - **SMTP Server Address:** Enter the SMTP server for your sender's email provider (e.g., `smtp.gmail.com`).
    - **SMTP Server Port:** Enter the SMTP port for your email provider (e.g., `587` for TLS).
3.  **Save Settings:** Tap the "Save Settings" button to store your configuration.
4.  **Start the Service:** Tap the "Start Monitoring Service" button. The app will now run in the background and forward any new SMS messages.
5.  **Stop the Service:** To stop forwarding messages, open the app and tap the "Stop Monitoring Service" button.

## Permissions

This app requires the following permissions to function correctly:

- `android.permission.RECEIVE_SMS`: To detect when a new SMS message has arrived on the device.
- `android.permission.READ_SMS`: To read the content of the incoming SMS message so it can be forwarded.
- `android.permission.INTERNET`: To connect to the SMTP server and send the email.
- `android.permission.FOREGROUND_SERVICE`: To allow the monitoring service to run reliably in the background.

## Building from Source

If you want to build the project from the source code:

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    ```
2.  **Open in Android Studio:** Open the cloned project directory in Android Studio.
3.  **Build:** Let Gradle sync and build the project. You can then run it on an emulator or a physical device.
