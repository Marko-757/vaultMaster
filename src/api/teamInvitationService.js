import axios from "axios";

const INVITATION_URL = "http://localhost:8080/api/team-invitations";

export const sendInvitation = (teamId, email) =>
  axios.post(`${INVITATION_URL}/send`, { teamId, email });

export const acceptInvitation = (code) =>
  axios.post(
    "http://localhost:8080/api/team-invitations/accept",
    { code },
    {
      withCredentials: true,
    }
  );
