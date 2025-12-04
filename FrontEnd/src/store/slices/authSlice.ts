import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { apiFetch } from '@/lib/apiClient';

export interface User {
  id: number;
  username: string;
  email: string;
  role: 'USER' | 'ADMIN';
}

interface AuthState {
  user: User | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  error: string | null;
  accessToken: string | null;
  refreshToken: string | null;
}

const accessTokenLS = typeof localStorage !== 'undefined' ? localStorage.getItem('accessToken') : null;
const refreshTokenLS = typeof localStorage !== 'undefined' ? localStorage.getItem('refreshToken') : null;
const userLS = typeof localStorage !== 'undefined' ? localStorage.getItem('user') : null;

const initialState: AuthState = {
  user: userLS ? JSON.parse(userLS) : null,
  isLoading: false,
  isAuthenticated: !!accessTokenLS,
  error: null,
  accessToken: accessTokenLS,
  refreshToken: refreshTokenLS,
};

export const loginUser = createAsyncThunk(
  'auth/loginUser',
  async (credentials: { username?: string; email?: string; password: string }) => {
    const response = await apiFetch('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials),
      skipAuth: true
    });
    return response.json();
  }
);

export const registerUser = createAsyncThunk(
  'auth/registerUser',
  async (userData: { username: string; email: string; password: string; role: string }) => {
    const response = await apiFetch('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify(userData),
      skipAuth: true
    });
    return response.json();
  }
);

export const refreshAccessToken = createAsyncThunk(
  'auth/refreshAccessToken',
  async () => {
    const refreshToken = typeof localStorage !== 'undefined' ? localStorage.getItem('refreshToken') : null;
    const response = await fetch('/api/auth/refresh', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken }),
    });
    if (!response.ok) {
      throw new Error('Token refresh failed');
    }
    return response.json();
  }
);

export const logoutUser = createAsyncThunk('auth/logoutUser', async () => {
  return null;
});

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    setUser: (state, action: PayloadAction<User>) => {
      state.user = action.payload;
      state.isAuthenticated = true;
    },
  },
  extraReducers: (builder) => {
    builder
      // Login cases
      .addCase(loginUser.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(loginUser.fulfilled, (state, action) => {
        state.isLoading = false;
        state.user = action.payload?.user || null;
        state.accessToken = action.payload?.accessToken || null;
        state.refreshToken = action.payload?.refreshToken || null;
        state.isAuthenticated = true;
        if (action.payload?.accessToken) {
          localStorage.setItem('accessToken', action.payload.accessToken);
        }
        if (action.payload?.refreshToken) {
          localStorage.setItem('refreshToken', action.payload.refreshToken);
        }
        if (action.payload?.user) {
          localStorage.setItem('user', JSON.stringify(action.payload.user));
        }
      })
      .addCase(loginUser.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.error.message || 'Login failed';
      })
      // Register cases
      .addCase(registerUser.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(registerUser.fulfilled, (state) => {
        state.isLoading = false;
      })
      .addCase(registerUser.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.error.message || 'Registration failed';
      })
      .addCase(refreshAccessToken.fulfilled, (state, action) => {
        state.accessToken = action.payload?.accessToken || null;
        if (action.payload?.accessToken) {
          localStorage.setItem('accessToken', action.payload.accessToken);
        }
      })
      // Logout cases
      .addCase(logoutUser.fulfilled, (state) => {
        state.user = null;
        state.isAuthenticated = false;
        state.accessToken = null;
        state.refreshToken = null;
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
      });
  },
});

export const { clearError, setUser } = authSlice.actions;
export default authSlice.reducer;
