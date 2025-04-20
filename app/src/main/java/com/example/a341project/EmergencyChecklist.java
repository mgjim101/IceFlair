package com.example.a341project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EmergencyChecklist extends AppCompatActivity {

    private CheckBox checkBoxWater, checkBoxFirstAid, checkBoxProtectiveClothing, checkBoxFireExtinguisher,
            checkBoxCommunicationDevice, checkBoxEmergencyBlanket, checkBoxNonPerishableFood;

    private CheckBox checkBoxSnowChains, checkBoxIceScraper, checkBoxWarmClothing, checkBoxJumperCables,
            checkBoxShovel, checkBoxFlashlight, checkBoxEmergencyFood;

    private LinearLayout wildfireChecklist, blackIceForm;
    private Switch switchToggle;

    Button home;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_checklist);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkBoxWater = findViewById(R.id.checkBoxWater);
        checkBoxFirstAid = findViewById(R.id.checkBoxFirstAid);
        checkBoxProtectiveClothing = findViewById(R.id.checkBoxProtectiveClothing);
        checkBoxFireExtinguisher = findViewById(R.id.checkBoxFireExtinguisher);
        checkBoxCommunicationDevice = findViewById(R.id.checkBoxCommunicationDevice);
        checkBoxEmergencyBlanket = findViewById(R.id.checkBoxEmergencyBlanket);
        checkBoxNonPerishableFood = findViewById(R.id.checkBoxNonPerishableFood);

        checkBoxSnowChains = findViewById(R.id.checkBoxSnowChains);
        checkBoxIceScraper = findViewById(R.id.checkBoxIceScraper);
        checkBoxWarmClothing = findViewById(R.id.checkBoxWarmClothing);
        checkBoxJumperCables = findViewById(R.id.checkBoxJumperCables);
        checkBoxShovel = findViewById(R.id.checkBoxShovel);
        checkBoxFlashlight = findViewById(R.id.checkBoxFlashlight);
        checkBoxEmergencyFood = findViewById(R.id.checkBoxEmergencyFood);

        switchToggle = findViewById(R.id.switchToggle);
        wildfireChecklist = findViewById(R.id.wildfireChecklist);
        blackIceForm = findViewById(R.id.blackIceForm);

        home = findViewById(R.id.home);
        home.setOnClickListener(view -> {
            Intent intent = new Intent(EmergencyChecklist.this, MainActivity.class);
            startActivity(intent);
        });

        switchToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                wildfireChecklist.setVisibility(LinearLayout.VISIBLE);
                blackIceForm.setVisibility(LinearLayout.GONE);
                switchToggle.setText("Wildfire Checklist");
            } else {
                wildfireChecklist.setVisibility(LinearLayout.GONE);
                blackIceForm.setVisibility(LinearLayout.VISIBLE);
                switchToggle.setText("Black Ice Checklist");
            }
        });

        Button buttonReset = findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(v -> resetChecklist());

        Button buttonShare = findViewById(R.id.buttonShare);
        buttonShare.setOnClickListener(v -> {
            if (validateRequiredFields()) {
                String checklistSummary = prepareChecklistSummary();
                if (checklistSummary != null) {
                    showEmailDialog("Emergency Checklist", checklistSummary);
                }
            }
        });
    }

    private boolean validateRequiredFields() {
        if (wildfireChecklist.getVisibility() == LinearLayout.VISIBLE) {
            if (!checkBoxWater.isChecked() || !checkBoxFirstAid.isChecked()) {
                Toast.makeText(this, "Water and First Aid Kit are required for Wildfire Checklist.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void resetChecklist() {
        if (wildfireChecklist.getVisibility() == LinearLayout.VISIBLE) {
            checkBoxWater.setChecked(false);
            checkBoxFirstAid.setChecked(false);
            checkBoxProtectiveClothing.setChecked(false);
            checkBoxFireExtinguisher.setChecked(false);
            checkBoxCommunicationDevice.setChecked(false);
            checkBoxEmergencyBlanket.setChecked(false);
            checkBoxNonPerishableFood.setChecked(false);
            Toast.makeText(this, "Wildfire Checklist Reset", Toast.LENGTH_SHORT).show();
        } else if (blackIceForm.getVisibility() == LinearLayout.VISIBLE) {
            checkBoxSnowChains.setChecked(false);
            checkBoxIceScraper.setChecked(false);
            checkBoxWarmClothing.setChecked(false);
            checkBoxJumperCables.setChecked(false);
            checkBoxShovel.setChecked(false);
            checkBoxFlashlight.setChecked(false);
            checkBoxEmergencyFood.setChecked(false);
            Toast.makeText(this, "Black Ice Checklist Reset", Toast.LENGTH_SHORT).show();
        }
    }

    private String prepareChecklistSummary() {
        StringBuilder checklistSummary = new StringBuilder();

        if (wildfireChecklist.getVisibility() == LinearLayout.VISIBLE) {
            checklistSummary.append("Wildfire Emergency Checklist:\n");
            checklistSummary.append("- [").append(checkBoxWater.isChecked() ? "✔" : "✘").append("] Water *\n");
            checklistSummary.append("- [").append(checkBoxFirstAid.isChecked() ? "✔" : "✘").append("] First Aid Kit *\n");
            checklistSummary.append("- [").append(checkBoxProtectiveClothing.isChecked() ? "✔" : "✘").append("] Protective Clothing\n");
            checklistSummary.append("- [").append(checkBoxFireExtinguisher.isChecked() ? "✔" : "✘").append("] Fire Extinguisher\n");
            checklistSummary.append("- [").append(checkBoxCommunicationDevice.isChecked() ? "✔" : "✘").append("] Communication Device\n");
            checklistSummary.append("- [").append(checkBoxEmergencyBlanket.isChecked() ? "✔" : "✘").append("] Emergency Blanket\n");
            checklistSummary.append("- [").append(checkBoxNonPerishableFood.isChecked() ? "✔" : "✘").append("] Non-Perishable Food\n");

        } else if (blackIceForm.getVisibility() == LinearLayout.VISIBLE) {
            checklistSummary.append("Black Ice and Snowstorm Checklist:\n");
            checklistSummary.append("- [").append(checkBoxSnowChains.isChecked() ? "✔" : "✘").append("] Snow Chains or Tires\n");
            checklistSummary.append("- [").append(checkBoxIceScraper.isChecked() ? "✔" : "✘").append("] Ice Scraper and De-Icer\n");
            checklistSummary.append("- [").append(checkBoxWarmClothing.isChecked() ? "✔" : "✘").append("] Blankets and Warm Clothing\n");
            checklistSummary.append("- [").append(checkBoxJumperCables.isChecked() ? "✔" : "✘").append("] Jumper Cables\n");
            checklistSummary.append("- [").append(checkBoxShovel.isChecked() ? "✔" : "✘").append("] Shovel\n");
            checklistSummary.append("- [").append(checkBoxFlashlight.isChecked() ? "✔" : "✘").append("] Flashlight and Batteries\n");
            checklistSummary.append("- [").append(checkBoxEmergencyFood.isChecked() ? "✔" : "✘").append("] Emergency Food and Water\n");
        }

        return checklistSummary.toString();
    }

    private void showEmailDialog(String subject, String body) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Send Checklist to Your Email");

        final EditText input = new EditText(this);
        input.setHint("Enter your email address");
        builder.setView(input);

        builder.setPositiveButton("Send", (dialog, which) -> {
            String emailAddress = input.getText().toString().trim();
            if (isValidEmail(emailAddress)) {
                sendEmailToAddress(emailAddress, subject, body);
                Toast.makeText(this, "Email sent to: " + emailAddress, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void sendEmailToAddress(String emailAddress, String subject, String body) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // Ensures only email apps handle this intent
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        emailIntent.setType("message-rfc822");
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(emailIntent, "Choose an email client:"));
        } else {
            //
        }
    }
}
