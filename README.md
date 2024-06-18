# BMI Calculator

BMI Calculator is an Android application that allows users to calculate their Body Mass Index (BMI) based on their height and weight. It also features Google Sign-In for authentication and tracks weight history over the past week with a graph visualization.

## Features

- **Google Sign-In Authentication**: Securely sign in with Google.
- **BMI Calculation**: Enter height and weight to calculate BMI.
- **BMI Category**: Determine BMI category based on the calculated BMI.
- **Edit User Details**: Update height and weight details.
- **Weight History Graph**: Visualize weight changes over the past week.



## Demo video

<p align="center">
  <a href="https://youtu.be/sCot_QjLnZs?feature=shared" target="_blank">
    <img src="https://github.com/Raghav354/BMI-Calculator/assets/137503421/06076232-a607-436c-96f3-beaf83be2026" alt="Demo Video" width="250"/>
  </a>
</p>

## Screenshots

<p align="center">
  <img src="https://github.com/Raghav354/BMI-Calculator/assets/137503421/3bb73f55-d406-4e5d-b48a-3545f45f1d54" alt="Login" width="200"/>
  <img src="https://github.com/Raghav354/BMI-Calculator/assets/137503421/af593734-c561-4651-a4b2-79d2b15b9dee" alt="User Details" width="200"/>
  <img src="https://github.com/Raghav354/BMI-Calculator/assets/137503421/404f2c20-3368-4e86-825b-523aa932f6ea" alt="Edit Details" width="200"/>
  <img src="https://github.com/Raghav354/BMI-Calculator/assets/137503421/67229676-e19e-40bf-987b-b1532bceb739" alt="Weight History Graph" width="200"/>
</p>

## Getting Started

These instructions will help you set up the project on your local machine for development and testing purposes.

### Prerequisites

- Android Studio
- Firebase account
- Google API Console project

### Installing

1. **Clone the repository**:
    ```bash
    git clone https://github.com/Rajat354/BMICalculator.git
    ```
2. **Open the project in Android Studio**.

3. **Configure Firebase**:
    - Go to the [Firebase Console](https://console.firebase.google.com/).
    - Create a new project or use an existing project.
    - Add an Android app to your project.
    - Download the `google-services.json` file and place it in the `app/` directory of your project.
    - Enable Firestore in the Firebase console.
    - Enable Google Sign-In in the Firebase Authentication section.

4. **Configure Google API Console**:
    - Go to the [Google API Console](https://console.developers.google.com/).
    - Create a new project or select an existing project.
    - Enable the Google Sign-In API.
    - Create an OAuth 2.0 Client ID and specify the package name of your app.
    - Add the SHA-1 signing certificate fingerprint from your app.

## Usage

1. **Launch the app**.
2. **Sign in with Google**.
3. **Enter your height and weight**.
4. **View your BMI and BMI category**.
5. **Edit your details if needed**.
6. **View your weight history graph**.

