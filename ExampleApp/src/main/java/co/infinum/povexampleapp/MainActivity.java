package co.infinum.povexampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
        Button commonUsage = findViewById(R.id.btn_open_common_usage);
        Button kotlinUsage = findViewById(R.id.btn_open_kotlin_common_usage);
        Button callUsage = findViewById(R.id.btn_open_call_usage);
        Button customParser = findViewById(R.id.btn_open_custom_parser);
        Button stream = findViewById(R.id.btn_open_stream_example);
        Button googleUpdates = findViewById(R.id.btn_open_google_inapp_updates);
        Button customChecker = findViewById(R.id.btn_open_custom_checker);
        commonUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCommonUsageClick();
            }
        });
        kotlinUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onKotlinCommonUsageClick();
            }
        });
        callUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCallUsageClick();
            }
        });
        customParser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCustomParserClick();
            }
        });
        stream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStreamLoaderClick();
            }
        });
        googleUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGoogleUpdatesClick();
            }
        });
        customChecker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCustomCheckerClick();
            }
        });
    }

    private void onCommonUsageClick() {
        startActivity(new Intent(this, CommonUsageExample.class));
    }

    private void onKotlinCommonUsageClick() {
        startActivity(new Intent(this, KotlinCommonExample.class));
    }

    private void onCallUsageClick() {
        startActivity(new Intent(this, CallUsageExample.class));
    }

    private void onCustomParserClick() {
        startActivity(new Intent(this, CustomParserExample.class));
    }

    private void onStreamLoaderClick() {
        startActivity(new Intent(this, StreamLoaderExample.class));
    }

    private void onGoogleUpdatesClick() {
        startActivity(new Intent(this, GoogleInAppUpdatesExample.class));
    }

    private void onCustomCheckerClick() {
        startActivity(new Intent(this, CustomRequirementCheckerExample.class));
    }
}
