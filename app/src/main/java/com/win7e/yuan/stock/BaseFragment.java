package com.win7e.yuan.stock;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.google.gson.Gson;
import com.win7e.yuan.stock.helper.AuthHelper;
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

        // Handle 401 Unauthorized
        if (response.code() == 401) {
            // Check if we are already in LoginFragment. If so, 401 means login failed, not session expired.
            boolean alreadyInLogin = false;
            try {
                alreadyInLogin = NavHostFragment.findNavController(this).getCurrentDestination() != null
                        && NavHostFragment.findNavController(this).getCurrentDestination().getId() == R.id.loginFragment;
            } catch (Exception ignored) {}

            if (!alreadyInLogin) {
                forceLogout();
                return;
            }
        }

        // For other HTTP errors (or 401 while on Login screen), try to parse the error message from the error body
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
    protected void forceLogout() {
        if (!isAdded() || getActivity() == null) return;

        Toast.makeText(requireContext(), "登录已过期，请重新登录", Toast.LENGTH_LONG).show();

        // Clear only session-related data. 
        // DO NOT use clear() because it would also remove the API Base URL and other settings.
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(AuthHelper.PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .remove(AuthHelper.KEY_TOKEN)
                .remove(AuthHelper.KEY_EXPIRE_TIME)
                .remove("name")
                .remove("role")
                .remove("base_id")
                .apply();

        // Navigate back to the LoginFragment, clearing the back stack
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true) // Pop up to the start of the graph, clearing the back stack.
                .build();

        NavHostFragment.findNavController(this).navigate(R.id.loginFragment, null, navOptions);
    }
}
