package com.androidproject.WattWaves;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.androidproject.WattWaves.databinding.ActivityCalculateElectricityTariffBinding;

import java.math.BigDecimal;

public class CalculateElectricityTariffActivity extends AppCompatActivity {

    private ActivityCalculateElectricityTariffBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalculateElectricityTariffBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding.toolbar.backIconIv.setVisibility(View.GONE);

        binding.calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBill();
            }
        });

        binding.toolbar.infoIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CalculateElectricityTariffActivity.this,AboutActivity.class);
                startActivity(i);
            }
        });

        binding.toolbar.shareIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create intent with ACTION_SEND action
                Intent shareIntent = new Intent(Intent.ACTION_SEND);

                // Set the type of content you want to share (text/plain for plain text)
                shareIntent.setType("text/plain");

                // Add the content you want to share
                String shareText = "Sharing this text with you!";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

                // Create a chooser intent to display a list of apps for sharing
                Intent chooserIntent = Intent.createChooser(shareIntent, "Share via");

                // Check if there are apps that can handle the sharing action
                if (shareIntent.resolveActivity(getPackageManager()) != null) {
                    // Start the activity with the chooser intent
                    startActivity(chooserIntent);
                } else {
                    // Handle the case where no apps can handle the sharing action
                    Toast.makeText(getApplicationContext(), "No apps available for sharing", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void calculateBill() {
        String unitsString = binding.unitsEditText.getText().toString();
        String rebateString = binding.rebateEditText.getText().toString();

        if (unitsString.isEmpty() || rebateString.isEmpty()) {
            showToast("Please enter all fields.");
            return;
        }

        double units = Double.parseDouble(unitsString);
        double rebate = Double.parseDouble(rebateString);

        if (units < 0 || rebate < 0 || rebate > 5) {
            showToast("Input Must be between 0 to 5 Percentage");
            return;
        }

        BigDecimal billAmount = calculateBillAmount(units, rebate);
        BigDecimal charges = calculateCharges(units); // Calculate charges before rebate

        binding.chargesTv.setText(String.format("Charges: RM %.2f", charges));
        binding.resultTextView.setText(String.format("Total Charges: RM %.2f", billAmount));
    }

    private BigDecimal calculateBillAmount(double units, double rebate) {
        BigDecimal billAmount = BigDecimal.ZERO;

        BigDecimal unitCost = new BigDecimal("0.218"); // Initial cost per kWh

        if (units <= 200) {
            billAmount = unitCost.multiply(BigDecimal.valueOf(units)); // 21.8 sen/kWh
        } else if (units <= 300) {
            BigDecimal first200Cost = new BigDecimal("200").multiply(unitCost); // First 200 kWh at 21.8 sen/kWh
            BigDecimal remainingUnits = BigDecimal.valueOf(units).subtract(new BigDecimal("200"));
            BigDecimal remainingCost = remainingUnits.multiply(new BigDecimal("0.334")); // Remaining units at 33.4 sen/kWh
            billAmount = first200Cost.add(remainingCost);
        } else if (units <= 600) {
            BigDecimal first300Cost = new BigDecimal("200").multiply(unitCost) // First 200 kWh at 21.8 sen/kWh
                    .add(new BigDecimal("100").multiply(new BigDecimal("0.334"))); // Next 100 kWh at 33.4 sen/kWh
            BigDecimal remainingUnits = BigDecimal.valueOf(units).subtract(new BigDecimal("300"));
            BigDecimal remainingCost = remainingUnits.multiply(new BigDecimal("0.516")); // Remaining units at 51.6 sen/kWh
            billAmount = first300Cost.add(remainingCost);
        }else if (units <= 900) {
            BigDecimal first600Cost = new BigDecimal("200").multiply(unitCost) // First 200 kWh at 21.8 sen/kWh
                    .add(new BigDecimal("100").multiply(new BigDecimal("0.334"))) // Next 100 kWh at 33.4 sen/kWh
                    .add(new BigDecimal("300").multiply(new BigDecimal("0.516"))); // Next 300 kWh at 51.6 sen/kWh
            BigDecimal remainingUnits = BigDecimal.valueOf(units).subtract(new BigDecimal("600"));
            BigDecimal remainingCost = remainingUnits.multiply(new BigDecimal("0.571")); // Remaining units at 57.1 sen/kWh
            billAmount = first600Cost.add(remainingCost);
        } else {
            BigDecimal first900Cost = new BigDecimal("200").multiply(unitCost) // First 200 kWh at 21.8 sen/kWh
                    .add(new BigDecimal("100").multiply(new BigDecimal("0.334"))) // Next 100 kWh at 33.4 sen/kWh
                    .add(new BigDecimal("300").multiply(new BigDecimal("0.516"))) // Next 300 kWh at 51.6 sen/kWh
                    .add(new BigDecimal("300").multiply(new BigDecimal("0.571"))); // Next 300 kWh at 57.1 sen/kWh
            BigDecimal remainingUnits = BigDecimal.valueOf(units).subtract(new BigDecimal("900"));
            BigDecimal remainingCost = remainingUnits.multiply(new BigDecimal("0.6")); // Remaining units at 60 sen/kWh
            billAmount = first900Cost.add(remainingCost);
        }

        // Deduct rebate percentage
        BigDecimal rebateAmount = billAmount.multiply(BigDecimal.valueOf(rebate).divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP));
        billAmount = billAmount.subtract(rebateAmount);

        return billAmount.setScale(2, BigDecimal.ROUND_HALF_UP); // Round to 2 decimal places
    }


    private BigDecimal calculateCharges(double units) {
        BigDecimal charges = BigDecimal.ZERO;

        BigDecimal unitCost = new BigDecimal("0.218"); // Initial cost per kWh

        if (units <= 200) {
            charges = unitCost.multiply(BigDecimal.valueOf(units)); // 21.8 sen/kWh
        } else if (units <= 300) {
            BigDecimal first200Cost = new BigDecimal("200").multiply(unitCost); // First 200 kWh at 21.8 sen/kWh
            BigDecimal remainingUnits = BigDecimal.valueOf(units).subtract(new BigDecimal("200"));
            BigDecimal remainingCost = remainingUnits.multiply(new BigDecimal("0.334")); // Remaining units at 33.4 sen/kWh
            charges = first200Cost.add(remainingCost);
        } else if (units <= 600) {
            BigDecimal first300Cost = new BigDecimal("200").multiply(unitCost) // First 200 kWh at 21.8 sen/kWh
                    .add(new BigDecimal("100").multiply(new BigDecimal("0.334"))); // Next 100 kWh at 33.4 sen/kWh
            BigDecimal remainingUnits = BigDecimal.valueOf(units).subtract(new BigDecimal("300"));
            BigDecimal remainingCost = remainingUnits.multiply(new BigDecimal("0.516")); // Remaining units at 51.6 sen/kWh
            charges = first300Cost.add(remainingCost);
        } else if (units <= 900) {
            BigDecimal first600Cost = new BigDecimal("200").multiply(unitCost) // First 200 kWh at 21.8 sen/kWh
                    .add(new BigDecimal("100").multiply(new BigDecimal("0.334"))) // Next 100 kWh at 33.4 sen/kWh
                    .add(new BigDecimal("300").multiply(new BigDecimal("0.516"))); // Next 300 kWh at 51.6 sen/kWh
            BigDecimal remainingUnits = BigDecimal.valueOf(units).subtract(new BigDecimal("600"));
            BigDecimal remainingCost = remainingUnits.multiply(new BigDecimal("0.571")); // Remaining units at 57.1 sen/kWh
            charges = first600Cost.add(remainingCost);
        } else {
            BigDecimal first900Cost = new BigDecimal("200").multiply(unitCost) // First 200 kWh at 21.8 sen/kWh
                    .add(new BigDecimal("100").multiply(new BigDecimal("0.334"))) // Next 100 kWh at 33.4 sen/kWh
                    .add(new BigDecimal("300").multiply(new BigDecimal("0.516"))) // Next 300 kWh at 51.6 sen/kWh
                    .add(new BigDecimal("300").multiply(new BigDecimal("0.571"))); // Next 300 kWh at 57.1 sen/kWh
            BigDecimal remainingUnits = BigDecimal.valueOf(units).subtract(new BigDecimal("900"));
            BigDecimal remainingCost = remainingUnits.multiply(new BigDecimal("0.6")); // Remaining units at 60 sen/kWh
            charges = first900Cost.add(remainingCost);
        }

        return charges.setScale(2, BigDecimal.ROUND_HALF_UP); // Round to 2 decimal places
    }





    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
