package psimos.jacob.dashboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DingDong extends AppCompatActivity {

    private Button btnChimeLR = null;
    private Button btnChimeRR = null;
    private Button btnChimeRF = null;
    private Button btnChimeLF = null;

    private Button btnBeepLR = null;
    private Button btnBeepRR = null;
    private Button btnBeepLF = null;
    private Button btnBeepRF = null;

    private Button btnFadeLR = null;
    private Button btnFadeRR = null;
    private Button btnFadeLF = null;
    private Button btnFadeRF = null;

    private Button btnBlinkLR = null;
    private Button btnBlinkRR = null;
    private Button btnBlinkLF = null;
    private Button btnBlinkRF = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dingdong);

        btnChimeLR = (Button)findViewById(R.id.btnChimeLR);
        btnChimeLR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnChimeLR_onClick();
            }
        });

        btnChimeRR = (Button)findViewById(R.id.btnChimeRR);
        btnChimeRR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnChimeRR_onClick();
            }
        });

        btnChimeLF = (Button)findViewById(R.id.btnChimeLF);
        btnChimeLF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnChimeLF_onClick();
            }
        });

        btnChimeRF = (Button)findViewById(R.id.btnChimeRF);
        btnChimeRF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnChimeRF_onClick();
            }
        });
        

        btnBeepLR = (Button)findViewById(R.id.btnBeepLR);
        btnBeepLR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBeepLR_onClick();
            }
        });

        btnBeepRR = (Button)findViewById(R.id.btnBeepRR);
        btnBeepRR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBeepRR_onClick();
            }
        });

        btnBeepLF = (Button)findViewById(R.id.btnBeepLF);
        btnBeepLF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBeepLF_onClick();
            }
        });

        btnBeepRF = (Button)findViewById(R.id.btnBeepRF);
        btnBeepRF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBeepRF_onClick();
            }
        });


        btnFadeLR = (Button)findViewById(R.id.btnFadeLR);
        btnFadeLR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFadeLR_onClick();
            }
        });

        btnFadeRR = (Button)findViewById(R.id.btnFadeRR);
        btnFadeRR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFadeRR_onClick();
            }
        });

        btnFadeLF = (Button)findViewById(R.id.btnFadeLF);
        btnFadeLF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFadeLF_onClick();
            }
        });

        btnFadeRF = (Button)findViewById(R.id.btnFadeRF);
        btnFadeRF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFadeRF_onClick();
            }
        });


        btnBlinkLR = (Button)findViewById(R.id.btnBlinkLR);
        btnBlinkLR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBlinkLR_onClick();
            }
        });

        btnBlinkRR = (Button)findViewById(R.id.btnBlinkRR);
        btnBlinkRR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBlinkRR_onClick();
            }
        });

        btnBlinkLF = (Button)findViewById(R.id.btnBlinkLF);
        btnBlinkLF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBlinkLF_onClick();
            }
        });

        btnBlinkRF = (Button)findViewById(R.id.btnBlinkRF);
        btnBlinkRF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBlinkRF_onClick();
            }
        });
        
    }

    private void btnBlinkRF_onClick() {

    }

    private void btnBlinkLF_onClick() {

    }

    private void btnBlinkRR_onClick() {

    }

    private void btnBlinkLR_onClick() {

    }

    private void btnFadeRF_onClick() {

    }

    private void btnFadeLF_onClick() {
        
    }

    private void btnFadeRR_onClick() {
        
    }

    private void btnFadeLR_onClick() {
        
    }

    private void btnBeepRF_onClick(){

    }

    private void btnBeepLF_onClick() {

    }

    private void btnBeepRR_onClick() {

    }

    private void btnBeepLR_onClick() {

    }

    private void btnChimeRF_onClick() {

    }

    private void btnChimeLF_onClick() {

    }

    private void btnChimeRR_onClick() {

    }

    private void btnChimeLR_onClick() {

    }
}
