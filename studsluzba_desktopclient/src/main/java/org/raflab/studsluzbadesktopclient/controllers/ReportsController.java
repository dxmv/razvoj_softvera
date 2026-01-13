package org.raflab.studsluzbadesktopclient.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.dtos.StudentDTO;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class ReportsController {

    private final StudentService studentService;

    ReportsController(StudentService studentService) {
        this.studentService = studentService;
    }

    public void handleIzvestajSviStudenti() throws JRException {
        generateReport(
                studentService.sviStudenti(),
                "/reports/sviStudenti.jrxml",
                "sviStudenti.pdf" );
    }

    @FXML
    TextField godinaUpisaTf;
    public void handleIzvestajSviStudentiPoGodiniUpisa() throws JRException {
        generateReport(
                studentService.searchStudentsByGodinaUpisa(Integer.parseInt(godinaUpisaTf.getText())),
                "/reports/sviStudenti.jrxml",
                "sviStudentiPoGodiniUpisa.pdf" );
    }

    @FXML
    TextField studProgTf;
    public void handleIzvestajSviStudentiPoStudProg() throws JRException {
        generateReport(
                studentService.searchStudentsByStudProg(studProgTf.getText()),
                "/reports/sviStudenti.jrxml",
                "sviStudentiPoStudProg.pdf" );
    }

    public void generateReport(List<StudentDTO> studenti, String reportPath, String pdfName) throws JRException {
        JasperReport report = JasperCompileManager.compileReport(MainView.class.getResourceAsStream(reportPath));
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(studenti);
        JasperPrint jp = JasperFillManager.fillReport(report, new HashMap<>(), dataSource);
        JasperExportManager.exportReportToPdfFile(jp, pdfName);
    }
}
