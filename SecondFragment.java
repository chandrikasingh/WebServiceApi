package com.test.webapi.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.test.webapi.R;
import com.test.webapi.databinding.FragmentSecondBinding;
import com.test.webapi.model.User;
import com.test.webapi.model.UserList;
import com.test.webapi.webcall.IWebApiHandler;
import com.test.webapi.webcall.WebApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize(view);
        setNavController();
        getRetrofitUserListApiCall();
    }

    private TextView mResponseLoggerText;
    private IWebApiHandler mIWebApiHandler;

    private void initialize(View Container) {
        mResponseLoggerText = (TextView) Container.findViewById(R.id.secondTxtResponse);
        mIWebApiHandler = WebApiClient.getClient().create(IWebApiHandler.class);
    }

    private void setNavController() {
        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }


    private void getRetrofitUserListApiCall() {

        /**
         Create new user
         **/
        User user = new User("morpheus", "leader");
        Call<User> call1 = mIWebApiHandler.createUser(user);
        call1.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user1 = response.body();
                Toast.makeText(getActivity(), user1.name + " " + user1.job + " " + user1.id + " " + user1.createdAt, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                call.cancel();
            }
        });

        /**
         GET List Users
         **/
        Call<UserList> call2 = mIWebApiHandler.doGetUserList("2");
        call2.enqueue(new Callback<UserList>() {
            @Override
            public void onResponse(Call<UserList> call, Response<UserList> response) {

                UserList userList = response.body();
                Integer text = userList.page;
                Integer total = userList.total;
                Integer totalPages = userList.totalPages;
                String pageInfo = text + " page\n" + total + " total\n" + totalPages + " totalPages\n";

                List<UserList.Datum> datumList = userList.data;

                StringBuffer buffer = new StringBuffer();
                buffer.append(pageInfo).append("\n");

                for (UserList.Datum datum : datumList) {
                    String userData = " Name: " + datum.first_name + " "+ datum.last_name + ", Id [ " + datum.id + "]"
                            + " \n Avatar: " + datum.avatar + "\n";
                    buffer.append(userData);
                }
                mResponseLoggerText.setText(buffer.toString());
            }

            @Override
            public void onFailure(Call<UserList> call, Throwable t) {
                call.cancel();
            }
        });


        /**
         POST name and job Url encoded.
         **/
        Call<UserList> call3 = mIWebApiHandler.doCreateUserWithField("morpheus","leader");
        call3.enqueue(new Callback<UserList>() {
            @Override
            public void onResponse(Call<UserList> call, Response<UserList> response) {
                UserList userList = response.body();
                Integer text = userList.page;
                Integer total = userList.total;
                Integer totalPages = userList.totalPages;
                String info = text + " page\n" + total + " total\n" + totalPages + " totalPages\n";
                List<UserList.Datum> datumList = userList.data;
                StringBuffer buffer = new StringBuffer();
                buffer.append(info);
                buffer.append("\n");

                for (UserList.Datum datum : datumList) {
                    String content = "id [" + datum.id + "] name: " + datum.first_name + " " + datum.last_name + " avatar: " + datum.avatar;
                    buffer.append(content).append("\n");
                }

                Toast.makeText(getActivity(),buffer.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<UserList> call, Throwable t) {
                call.cancel();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}