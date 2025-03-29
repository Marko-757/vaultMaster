Program running and testing directions:

Set IntelliJ environment variables: Expand where you would run "VaultApplication" -> Edit Configurations -> Modify Options -> Under Operating Systems, click Environment Variables

Run the application (can use the green triangle outline next to the Application name)

Test in order in Postman:

1. Sign-up (Sign up with an accessible email)
   POST http://localhost:8080/api/auth/register
   JSON Body:
   {
    "email": "",
    "passwordHash": "",
    "fullName": "",
    "phoneNumber": ""
   }
2. Login
   POST http://localhost:8080/api/auth/login
   JSON Body:
   {
    "email": "",
    "password": ""
    }

If successful OTP will be sent to the account email and Postman will return "OTP sent to your email. Please verify to complete login."
If not successful error will be returned - check the IntelliJ debug in the Run section

3. Verify Account
POST http://localhost:8080/api/2fa/verify?userId={USER_ID}&otp={6-DIGIT OTP}
