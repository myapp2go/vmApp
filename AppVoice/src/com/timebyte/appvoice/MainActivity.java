package com.timebyte.appvoice;

import java.util.Arrays;
import java.util.List;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
/*
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
*/
public class MainActivity extends FragmentActivity {

//	private LoginButton loginBtn;
	private Button postImageBtn;
	private Button updateStatusBtn;

	private TextView userName;

//	private UiLifecycleHelper uiHelper;

	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");

	private static String message = "Sample status posted from android app";

	private CallbackManager callbackManager;
	private LoginButton loginButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		FacebookSdk.sdkInitialize(this.getApplicationContext());

		callbackManager = CallbackManager.Factory.create();
		/*
	    LoginManager.getInstance().registerCallback(callbackManager,
	            new FacebookCallback<LoginResult>() {
	                @Override
	                public void onSuccess(LoginResult loginResult) {
	                    // App code
	                	System.out.println("**** onSuccess");
	                }

	                @Override
	                public void onCancel() {
	                     // App code
	                	System.out.println("**** onCancel");
	                }

	                @Override
	                public void onError(FacebookException exception) {
	                     // App code  
	                	System.out.println("**** onError");
	                }
	    });
*/	    
		loginButton = (LoginButton) findViewById(R.id.login_button);
		
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

			@Override
			public void onSuccess(LoginResult result) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(FacebookException error) {
				// TODO Auto-generated method stub
				
			}  });

//		uiHelper = new UiLifecycleHelper(this, statusCallback);
//		uiHelper.onCreate(savedInstanceState);

//		setContentView(R.layout.activity_main);

		userName = (TextView) findViewById(R.id.user_name);
/*
		loginBtn = (LoginButton) findViewById(R.id.fb_login_button);
		loginBtn.setUserInfoChangedCallback(new UserInfoChangedCallback() {
			@Override
			public void onUserInfoFetched(GraphUser user) {
				if (user != null) {
					userName.setText("Hello, " + user.getName());
				} else {
					userName.setText("You are not logged");
				}
			}
		});
*/
		postImageBtn = (Button) findViewById(R.id.post_image);
		postImageBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
//				postImage();
			}
		});

		updateStatusBtn = (Button) findViewById(R.id.update_status);
		updateStatusBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

//		buttonsEnabled(false);
	}
	
	public View onCreateView(
	        LayoutInflater inflater,
	        ViewGroup container,
	        Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.splash, container, false);

//	    LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
	    loginButton.setReadPermissions("user_friends");
	    // If using in a fragment
	    loginButton.setFragment(this);    
	    // Other app specific specialization

	    // Callback registration
	    loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
	        @Override
	        public void onSuccess(LoginResult loginResult) {
	            // App code
	        }

	        @Override
	        public void onCancel() {
	            // App code
	        }

	        @Override
	        public void onError(FacebookException exception) {
	            // App code
	        }
	    });    
	}
	
/*
	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			if (state.isOpened()) {
				buttonsEnabled(true);
				Log.d("FacebookSampleActivity", "Facebook session opened");
			} else if (state.isClosed()) {
				buttonsEnabled(false);
				Log.d("FacebookSampleActivity", "Facebook session closed");
			}
		}
	};

	public void buttonsEnabled(boolean isEnabled) {
		postImageBtn.setEnabled(isEnabled);
		updateStatusBtn.setEnabled(isEnabled);
	}

	public void postImage() {
		if (checkPermissions()) {
			Bitmap img = BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_launcher);
			Request uploadRequest = Request.newUploadPhotoRequest(
					Session.getActiveSession(), img, new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							Toast.makeText(MainActivity.this,
									"Photo uploaded successfully",
									Toast.LENGTH_LONG).show();
						}
					});
			uploadRequest.executeAsync();
		} else {
			requestPermissions();
		}
	}

	public void postStatusMessage() {
		if (checkPermissions()) {
			Request request = Request.newStatusUpdateRequest(
					Session.getActiveSession(), message,
					new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							if (response.getError() == null)
								Toast.makeText(MainActivity.this,
										"Status updated successfully",
										Toast.LENGTH_LONG).show();
						}
					});
			request.executeAsync();
		} else {
			requestPermissions();
		}
	}

	public boolean checkPermissions() {
		Session s = Session.getActiveSession();
		if (s != null) {
			return s.getPermissions().contains("publish_actions");
		} else
			return false;
	}

	public void requestPermissions() {
		Session s = Session.getActiveSession();
		if (s != null)
			s.requestNewPublishPermissions(new Session.NewPermissionsRequest(
					this, PERMISSIONS));
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		buttonsEnabled(Session.getActiveSession().isOpened());
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
		uiHelper.onSaveInstanceState(savedState);
	}
*/	
}
