package org.raflab.studsluzbadesktopclient.services;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.model.reports.EnrollmentCertificateData;
import org.raflab.studsluzbadesktopclient.model.reports.ExamByYearGroup;
import org.raflab.studsluzbadesktopclient.model.reports.PassedExamCertificateData;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CertificateService {

    public void generateEnrollmentCertificate(EnrollmentCertificateData data, String filename) throws JRException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ime", data.getIme());
        parameters.put("prezime", data.getPrezime());
        parameters.put("srednjeIme", data.getSrednjeIme());
        parameters.put("indeks", data.getIndeks());
        parameters.put("jmbg", data.getJmbg());
        parameters.put("datumRodjenja", data.getDatumRodjenja() != null ? data.getDatumRodjenja().toString() : "");
        parameters.put("mestoRodjenja", data.getMestoRodjenja());
        parameters.put("drzavaRodjenja", data.getDrzavaRodjenja());
        parameters.put("studijskiProgram", data.getStudijskiProgram());
        parameters.put("fakultet", data.getFakultet());
        parameters.put("trenutniStatus", data.getTrenutniStatus());
        
        JRBeanCollectionDataSource enrollmentsDataSource = new JRBeanCollectionDataSource(
                data.getUpisaneGodine() != null ? data.getUpisaneGodine() : Collections.emptyList()
        );
        parameters.put("upisaneGodineDataSource", enrollmentsDataSource);
        
        JRBeanCollectionDataSource renewalsDataSource = new JRBeanCollectionDataSource(
                data.getObnovljeneGodine() != null ? data.getObnovljeneGodine() : Collections.emptyList()
        );
        parameters.put("obnovljeneGodineDataSource", renewalsDataSource);
        
        generateReport(Collections.singletonList(data), parameters, "/reports/uverenjeOStudiranju.jrxml", filename);
    }

    public void generatePassedExamsCertificate(PassedExamCertificateData data, String filename) throws JRException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ime", data.getIme());
        parameters.put("prezime", data.getPrezime());
        parameters.put("srednjeIme", data.getSrednjeIme());
        parameters.put("indeks", data.getIndeks());
        
        // Flatten exam groups into a single list with year information
        List<FlatExamRecord> flatExams = new java.util.ArrayList<>();
        if (data.getExamsByYear() != null) {
            for (ExamByYearGroup yearGroup : data.getExamsByYear()) {
                Integer year = yearGroup.getGodinaStudija();
                if (yearGroup.getIspiti() != null) {
                    for (org.raflab.studsluzbadesktopclient.model.reports.ExamDetails exam : yearGroup.getIspiti()) {
                        flatExams.add(new FlatExamRecord(
                                exam.getSifraPredmeta(),
                                exam.getNazivPredmeta(),
                                exam.getOcena(),
                                exam.getEspbBodovi(),
                                year
                        ));
                    }
                }
            }
        }
        
        generateReport(flatExams, parameters, "/reports/uverenjeOPolozenim.jrxml", filename);
    }
    
    // Inner class for flattened exam data
    public static class FlatExamRecord {
        private String sifraPredmeta;
        private String nazivPredmeta;
        private Integer ocena;
        private Integer espbBodovi;
        private Integer godinaStudija;
        
        public FlatExamRecord(String sifraPredmeta, String nazivPredmeta, Integer ocena, Integer espbBodovi, Integer godinaStudija) {
            this.sifraPredmeta = sifraPredmeta;
            this.nazivPredmeta = nazivPredmeta;
            this.ocena = ocena;
            this.espbBodovi = espbBodovi;
            this.godinaStudija = godinaStudija;
        }
        
        public String getSifraPredmeta() { return sifraPredmeta; }
        public String getNazivPredmeta() { return nazivPredmeta; }
        public Integer getOcena() { return ocena; }
        public Integer getEspbBodovi() { return espbBodovi; }
        public Integer getGodinaStudija() { return godinaStudija; }
    }

    private void generateReport(List<?> dataList, Map<String, Object> parameters, 
                                 String jrxmlPath, String outputFilename) throws JRException {
        JasperReport report = JasperCompileManager.compileReport(MainView.class.getResourceAsStream(jrxmlPath));
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);
        JasperPrint jp = JasperFillManager.fillReport(report, parameters, dataSource);
        JasperExportManager.exportReportToPdfFile(jp, outputFilename);
    }
}
