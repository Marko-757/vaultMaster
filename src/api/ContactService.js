import axios from "axios";

const API_URL = 'http://localhost:8080/register';

export async function saveUser(user) {
    return await axios.post(API_URL, user);
}

export async function getUser(page = 0, size = 10) {
    return await axios.get(`${API_URL}?page=${page}&size=${size}`);
}