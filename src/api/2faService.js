export async function verifyOtp(otp) {
  const url = `/api/2fa/verify?otp=${otp}`; // No need for `userId` here anymore

  const response = await fetch(url, {
    method: "POST",
    credentials: "include", // include the JWT cookie
  });

  if (!response.ok) {
    throw new Error("OTP verification failed");
  }

  return response.text(); // or JSON if your backend returns more
}
