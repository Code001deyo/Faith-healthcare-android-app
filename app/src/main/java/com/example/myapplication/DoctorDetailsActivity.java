package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class DoctorDetailsActivity extends AppCompatActivity {
    private String[][] doctor_details1 = {
            {"Doctor Name: Dr. Sarah Johnson", "Hospital: Faith Medical Center", "Experience: 12 years", "Specialization: Family Medicine", "Contact: +254 722 123 456", "Fee: KES 2,500"},
            {"Doctor Name: Dr. Michael Chen", "Hospital: Faith Healthcare Clinic", "Experience: 15 years", "Specialization: Internal Medicine", "Contact: +254 733 234 567", "Fee: KES 3,000"},
            {"Doctor Name: Dr. Emily Williams", "Hospital: Faith Community Hospital", "Experience: 8 years", "Specialization: Pediatrics", "Contact: +254 722 345 678", "Fee: KES 2,000"}
    };

    private String[][] doctor_details2 = {
            {"Doctor Name: Dr. Rachel Green", "Hospital: Faith Nutrition Center", "Experience: 10 years", "Specialization: Clinical Nutrition", "Contact: +254 733 456 789", "Fee: KES 2,000"},
            {"Doctor Name: Dr. David Miller", "Hospital: Faith Wellness Clinic", "Experience: 8 years", "Specialization: Sports Nutrition", "Contact: +254 722 567 890", "Fee: KES 2,500"},
            {"Doctor Name: Dr. Lisa Thompson", "Hospital: Faith Health Center", "Experience: 12 years", "Specialization: Pediatric Nutrition", "Contact: +254 733 678 901", "Fee: KES 2,200"}
    };

    private String[][] doctor_details3 = {
            {"Doctor Name: Dr. James Wilson", "Hospital: Faith Dental Care", "Experience: 15 years", "Specialization: Orthodontics", "Contact: +254 722 789 012", "Fee: KES 3,500"},
            {"Doctor Name: Dr. Maria Garcia", "Hospital: Faith Smile Center", "Experience: 10 years", "Specialization: Pediatric Dentistry", "Contact: +254 733 890 123", "Fee: KES 3,000"},
            {"Doctor Name: Dr. Robert Brown", "Hospital: Faith Oral Health", "Experience: 12 years", "Specialization: Periodontics", "Contact: +254 722 901 234", "Fee: KES 3,200"}
    };

    private String[][] doctor_details4 = {
            {"Doctor Name: Dr. John Smith", "Hospital: Faith Surgical Center", "Experience: 20 years", "Specialization: General Surgery", "Contact: +254 733 012 345", "Fee: KES 5,000"},
            {"Doctor Name: Dr. Patricia Lee", "Hospital: Faith Medical Center", "Experience: 15 years", "Specialization: Cardiothoracic Surgery", "Contact: +254 722 123 456", "Fee: KES 6,000"},
            {"Doctor Name: Dr. William Davis", "Hospital: Faith Specialty Hospital", "Experience: 18 years", "Specialization: Neurosurgery", "Contact: +254 733 234 567", "Fee: KES 7,000"}
    };

    private String[][] doctor_details5 = {
            {"Doctor Name: Dr. Thomas Anderson", "Hospital: Faith Heart Center", "Experience: 18 years", "Specialization: Interventional Cardiology", "Contact: +254 722 345 678", "Fee: KES 4,500"},
            {"Doctor Name: Dr. Elizabeth Taylor", "Hospital: Faith Medical Center", "Experience: 15 years", "Specialization: Electrophysiology", "Contact: +254 733 456 789", "Fee: KES 5,000"},
            {"Doctor Name: Dr. Richard White", "Hospital: Faith Cardiac Care", "Experience: 20 years", "Specialization: Pediatric Cardiology", "Contact: +254 722 567 890", "Fee: KES 5,500"}
    };

    TextView tv;
    Button btn;
    String[][] doctor_details = {};
    ArrayList<HashMap<String, String>> list;
    SimpleAdapter sa;
    ListView lst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details);
        tv = findViewById(R.id.listViewCart);
        btn = findViewById(R.id.buttonBMToCart);
        lst = findViewById(R.id.listViewBM);

        Intent it = getIntent();
        String title = it.getStringExtra("title");
        tv.setText(title);

        if (title.compareTo("Family Physicians") == 0)
            doctor_details = doctor_details1;
        else if (title.compareTo("Dietician") == 0)
            doctor_details = doctor_details2;
        else if (title.compareTo("Dentist") == 0)
            doctor_details = doctor_details3;
        else if (title.compareTo("Surgeon") == 0)
            doctor_details = doctor_details4;
        else
            doctor_details = doctor_details5;

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DoctorDetailsActivity.this, BookAppointmentActivity.class));
            }
        });

        list = new ArrayList<>();
        for (int idx = 0; idx < doctor_details.length; idx++) {
            String[] doctor_detail = doctor_details[idx];
            HashMap<String, String> item = new HashMap<>();
            item.put("line1", doctor_detail[0]);
            item.put("line2", doctor_detail[1]);
            item.put("line3", doctor_detail[2]);
            item.put("line4", doctor_detail[3]);
            item.put("line5", doctor_detail[4]);
            item.put("line6", doctor_detail[5]);
            // Set doctor image by gender (male/female)
            String name = doctor_detail[0].toLowerCase();
            if (name.contains("sarah") || name.contains("emily") || name.contains("rachel") || name.contains("lisa") || name.contains("maria") || name.contains("patricia") || name.contains("elizabeth")) {
                item.put("image", "physician_female");
            } else {
                item.put("image", "physician_male");
            }
            list.add(item);
        }

        sa = new SimpleAdapter(
            this,
            list,
            R.layout.multi_lines,
            new String[]{"line1", "line2", "line3", "line4", "line5", "line6", "image"},
            new int[]{R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_d, R.id.line_e, R.id.line_d, R.id.medicine_image}
        );
        sa.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view.getId() == R.id.medicine_image && data instanceof String) {
                    String imgName = (String) data;
                    int resId = getResources().getIdentifier(imgName, "drawable", getPackageName());
                    ((ImageView) view).setImageResource(resId);
                    return true;
                }
                return false;
            }
        });

        lst.setAdapter(sa);

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent it = new Intent(DoctorDetailsActivity.this, BookAppointmentActivity.class);
                it.putExtra("text1", title);
                it.putExtra("text2", doctor_details[i][0]);
                it.putExtra("text3", doctor_details[i][1]);
                it.putExtra("text4", doctor_details[i][4]);
                it.putExtra("text5", doctor_details[i][5]);
                startActivity(it);
            }
        });
    }
}