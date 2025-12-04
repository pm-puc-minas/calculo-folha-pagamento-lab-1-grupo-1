const API_BASE = import.meta.env.VITE_API_BASE_URL ?? "";

type FetchOptions = RequestInit & { skipAuth?: boolean };

const clearSession = () => {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("refreshToken");
  localStorage.removeItem("user");
};

export const apiFetch = async (path: string, options: FetchOptions = {}) => {
  const headers = new Headers(options.headers || {});
  const token = localStorage.getItem("accessToken");

  if (token && !options.skipAuth) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  // evita setar content-type para FormData
  if (!(options.body instanceof FormData) && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  const response = await fetch(`${API_BASE}${path}`, { ...options, headers });

  if (response.status === 401 || response.status === 403) {
    clearSession();
    throw new Error("Unauthorized");
  }

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `Request failed (${response.status})`);
  }

  return response;
};
