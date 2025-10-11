package com.example.jobportal.data.pojo;

import com.example.jobportal.data.entity.CvSection;
import com.example.jobportal.data.entity.UserCv;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FullCvResponse {
    private UserCv cv;
    private List<CvSection> sections;
}

