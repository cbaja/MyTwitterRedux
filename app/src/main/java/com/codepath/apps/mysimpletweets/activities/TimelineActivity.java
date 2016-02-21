package com.codepath.apps.mysimpletweets.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adapters.TweeterPagerAdapter;
import com.codepath.apps.mysimpletweets.adapters.TweetsAdapter;
import com.codepath.apps.mysimpletweets.dialogs.PostTweetDialog;
import com.codepath.apps.mysimpletweets.fragments.TweetListFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.astuetz.PagerSlidingTabStrip;


import org.apache.http.Header;
//import cz.msebera.android.httpclient.Header;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity implements PostTweetDialog.PostTweetDialogListener, TweetListFragment.TweetListFragmentListener {
    private static final int POST_TWEET_REQUEST_CODE = 341;

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsAdapter aTweets;
    private ListView lvTweets;

    private TweetListFragment tweetListFragment;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add tweeter icon to actionbar
     //   getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.tweeter_icon);
     //   getSupportActionBar().setDisplayUseLogoEnabled(true);

        client = TwitterApplication.getRestClient();
        getUserProfile();

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new TweeterPagerAdapter(getSupportFragmentManager()));

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);
    }

    private void getUserProfile() {
        client.getUserProfile(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                Log.d("DEBUG", "user: " + jsonObject.toString());
                user = new User(jsonObject);
            }


            @Override
            public void onFailure(int statusCode,
                                  Header[] headers,
                                  Throwable throwable,
                                  JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_tweet) {
            FragmentManager fm = getSupportFragmentManager();
            PostTweetDialog postTweetDialog = PostTweetDialog.newInstance(user, null);
            postTweetDialog.show(fm, "fragment_post_tweet");
        } else if (id == R.id.action_profile) {
            showUserProfile(user);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPost(Tweet tweet) {
        if (tweet != null) {
            // pass tweet to tweet list fragment
            tweetListFragment.insertTweet(tweet);
        }
    }

    @Override
    public void onReplyTweet(Tweet tweet) {
        FragmentManager fm = getSupportFragmentManager();
        PostTweetDialog postTweetDialog = PostTweetDialog.newInstance(user, tweet);
        postTweetDialog.show(fm, "fragment_reply_tweet");
    }

    @Override
    public void onViewUserProfile(User user) {
        if (user != null) {
            showUserProfile(user);
        }
    }

    private void showUserProfile(User user) {
        Intent userProfileIntent = new Intent(this, UserProfileActivity.class);
        userProfileIntent.putExtra("user", user);
        startActivity(userProfileIntent);

    }
}
