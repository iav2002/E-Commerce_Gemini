import axios from "axios";

const API = axios.create({
  baseURL: "",
});
delete API.defaults.headers.common["Authorization"];
export default API;
