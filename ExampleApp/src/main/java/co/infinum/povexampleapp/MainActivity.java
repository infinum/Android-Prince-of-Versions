package co.infinum.povexampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_open_common_usage)
    public void onCommonUsageClick() {
        startActivity(new Intent(this, CommonUsageExample.class));
    }

    @OnClick(R.id.btn_open_network_with_login)
    public void onNetworkWithLoginClick() {
        startActivity(new Intent(this, NetworkWithLoginExample.class));
    }

    @OnClick(R.id.btn_open_custom_parser)
    public void onCustomParserClick() {
        startActivity(new Intent(this, CustomParserExample.class));
    }

    @OnClick(R.id.btn_open_stream_example)
    public void onStreamLoaderClick() {
        startActivity(new Intent(this, StreamLoaderExample.class));
    }
}
