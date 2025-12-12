package com.win7e.yuan.stock;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.win7e.yuan.stock.model.LoginResponse; // Reusing a simple response model for parsing error messages

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

public abstract class BaseFragment extends Fragment {

    /**
     * A centralized handler for API response errors.
     * It checks for 401 Unauthorized errors to force logout and parses the error body for other errors.
     *
     * @param response The response from the API call.
     */
    protected void handleApiError(Response<?> response) {
        if (!isAdded() || getContext() == null) return;

        // Handle 401 Unauthorized specifically
        if (response.code() == 401) {
            forceLogout();
            return;
        }

        // For other HTTP errors, try to parse the error message from the error body
        String errorMessage = "操作失败，请重试。";
        if (response.errorBody() != null) {
            try (ResponseBody errorBody = response.errorBody()) {
                Gson gson = new Gson();
                // We can reuse a simple response model like LoginResponse to get the message
                LoginResponse errorResponse = gson.fromJson(errorBody.string(), LoginResponse.class);
                if (errorResponse != null && errorResponse.getMessage() != null && !errorResponse.getMessage().isEmpty()) {
                    errorMessage = errorResponse.getMessage();
                }
            } catch (IOException e) {
                // Ignore, and use the generic error message
            }
        }
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    /**
     * Handles network failures (e.g., no internet connection).
     *
     * @param t The throwable from the onFailure callback.
     */
    protected void handleApiFailure(Throwable t) {
        if (!isAdded() || getContext() == null) return;
        Toast.makeText(requireContext(), "网络错误，请检查您的连接", Toast.LENGTH_SHORT).show();
    }

    /**
     * Clears user session and navigates back to the Login screen.
     */
    private void forceLogout() {
        if (!isAdded() || getActivity() == null) return;

        Toast.makeText(requireContext(), "登录已过期，请重新登录", Toast.LENGTH_LONG).show();

        // Clear all stored user data
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        // Navigate back to the LoginFragment, clearing the back stack
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }
}
