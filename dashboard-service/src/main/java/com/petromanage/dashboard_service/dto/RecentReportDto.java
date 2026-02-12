// package com.petromanage.dashboard_service.dto;

// public class RecentReportDto {
    
// }


package com.petromanage.dashboard_service.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecentReportDto {
    private String reportId;
    private double productionEfficiency;
    private double downtime;
    private String generatedDate;
}