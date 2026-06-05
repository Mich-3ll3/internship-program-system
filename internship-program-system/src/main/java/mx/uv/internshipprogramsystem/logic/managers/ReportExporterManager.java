package mx.uv.internshipprogramsystem.logic.managers;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import mx.uv.internshipprogramsystem.logic.dto.MonthlyReportContextDTO;
import mx.uv.internshipprogramsystem.logic.dto.ReportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportExporterManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportExporterManager.class);
    private static final String PDF_EXTENSION = ".pdf";
    private static final String FILE_PREFIX = "Reporte_Mensual_No_";

    public boolean generatePlainPdfReport(ReportDTO report, MonthlyReportContextDTO context, String destinationFilePath) {
        
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(destinationFilePath));
            document.open();

            document.add(new Paragraph("DATOS GENERALES DE LA EE"));
            document.add(new Paragraph("1.- Carrera: " + context.getMajor()));
            document.add(new Paragraph("2.- NRC: " + context.getNrc()));
            document.add(new Paragraph("3.- Profesor: " + context.getProfessorName()));
            document.add(new Paragraph("4.- Periodo Escolar: " + context.getSchoolPeriod()));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("DATOS DEL PROYECTO"));

            String internsList = context.getFormattedInternNames();
            if (internsList == null || internsList.trim().isEmpty()) {
                internsList = "No disponible";
            }
            
            document.add(new Paragraph("1.- Alumno(s): " + internsList));
            document.add(new Paragraph("2.- Organización Vinculada: " + context.getOrganizationName()));
            document.add(new Paragraph("3.- Proyecto: " + context.getProjectName()));
            document.add(new Paragraph("4.- Periodo del reporte y horas: " + context.getSchoolPeriod() + " | " + context.getAccumulatedHours() + " hrs"));
            document.add(new Paragraph("5.- Fecha del reporte: " + report.getDate().toString()));
            document.add(new Paragraph("6.- Número del Informe: " + report.getNumber()));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("DESARROLLO"));
            document.add(new Paragraph("1.- Objetivo(s) general del proyecto: " + context.getGeneralObjective()));
            document.add(new Paragraph("2.- Metodología: " + context.getMethodology()));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("AVANCES"));
            document.add(new Paragraph("1.- Avance de actividades realizadas: (Tabla pendiente de formatear con JasperReports)"));
            document.add(new Paragraph("2.- Resultados obtenidos al momento: " + report.getCurrentResults()));
            document.add(new Paragraph("3.- Observaciones: " + report.getParticularObservations()));

            document.close();
            LOGGER.info("PDF generado exitosamente en la ruta: {}", destinationFilePath);
            return true;

        } catch (DocumentException | IOException exception) {
            LOGGER.error("Error al generar el documento PDF: {}", exception.getMessage(), exception);
            return false;
        }
    }
}