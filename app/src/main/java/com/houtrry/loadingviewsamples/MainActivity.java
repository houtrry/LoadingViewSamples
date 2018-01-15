package com.houtrry.loadingviewsamples;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private LoadingView mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingView = (LoadingView) findViewById(R.id.loadingView);

        findViewById(R.id.startOrStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoadingView.isRunningAnimator()) {
                    mLoadingView.stopAnimator();
                } else {
                    mLoadingView.startAnimator();
                }
            }
        });
    }
}
