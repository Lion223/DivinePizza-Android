package com.github.lion223.divinepizza;

import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ActivityViewTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);

    private LoginActivity mActivity = null;

    @Before
    public void setUp(){
        mActivity = mActivityRule.getActivity();
    }

    @Test
    public void viewTest(){
        Assert.assertNotNull(mActivity.findViewById(R.id.phone_number));
    }

    @After
    public void tearDown(){
        mActivity = null;
    }
}
