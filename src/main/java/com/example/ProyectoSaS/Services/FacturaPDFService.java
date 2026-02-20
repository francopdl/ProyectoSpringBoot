package com.example.ProyectoSaS.Services;

import com.example.ProyectoSaS.models.Factura;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FacturaPDFService {

    public byte[] generarPDFFactura(Factura factura, String nombrePais) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        
        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        
        // Title
        Paragraph title = new Paragraph("FACTURA")
            .setFont(boldFont)
            .setFontSize(28)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(title);
        
        // Invoice Details
        document.add(new Paragraph()
            .add("Número de Factura: ").setFont(boldFont)
            .add(factura.getNumeroFactura()).setFont(regularFont));
        document.add(new Paragraph()
            .add("Estado: ").setFont(boldFont)
            .add(factura.getEstado()).setFont(regularFont));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        document.add(new Paragraph()
            .add("Fecha de Emisión: ").setFont(boldFont)
            .add(factura.getFechaEmision().format(formatter)).setFont(regularFont));
        document.add(new Paragraph()
            .add("Fecha de Vencimiento: ").setFont(boldFont)
            .add(factura.getFechaVencimiento().format(formatter)).setFont(regularFont));
        
        document.add(new Paragraph(""));
        
        // Customer Info
        document.add(new Paragraph("DATOS DEL CLIENTE").setFont(boldFont).setFontSize(12));
        document.add(new Paragraph()
            .add("Nombre: ").setFont(boldFont)
            .add(factura.getUsuario().getNombre() + " " + factura.getUsuario().getApellido()).setFont(regularFont));
        document.add(new Paragraph()
            .add("Email: ").setFont(boldFont)
            .add(factura.getUsuario().getEmail()).setFont(regularFont));
        document.add(new Paragraph()
            .add("País: ").setFont(boldFont)
            .add(nombrePais).setFont(regularFont));
        
        document.add(new Paragraph(""));
        
        // Summary Table
        float[] columnWidths = {2f, 2f};
        Table table = new Table(columnWidths);
        
        table.addCell(new Cell().add(new Paragraph("CONCEPTO").setFont(boldFont)));
        table.addCell(new Cell().add(new Paragraph("MONTO (€)").setFont(boldFont)));
        
        table.addCell(new Cell().add(new Paragraph("Base Imponible")));
        table.addCell(new Cell().add(new Paragraph(String.format("%.2f", factura.getMonto()))));
        
        table.addCell(new Cell().add(new Paragraph("Impuesto (" + factura.getPorcentajeImpuesto() + "%)")));
        table.addCell(new Cell().add(new Paragraph(String.format("%.2f", factura.getMontoImpuesto()))));
        
        table.addCell(new Cell().add(new Paragraph("TOTAL A PAGAR").setFont(boldFont)));
        table.addCell(new Cell().add(new Paragraph(String.format("€ %.2f", factura.getTotalConImpuesto())).setFont(boldFont)));
        
        document.add(table);
        document.add(new Paragraph(""));
        document.add(new Paragraph("Documento generado automáticamente por ProyectoSaS")
            .setFont(regularFont).setFontSize(9).setTextAlignment(TextAlignment.CENTER));
        
        document.close();
        return outputStream.toByteArray();
    }

    public byte[] generarPDFMultiplesFacturas(List<Factura> facturas) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        
        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        
        // Title
        Paragraph title = new Paragraph("REPORTE DE FACTURAS")
            .setFont(boldFont)
            .setFontSize(24)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(title);
        document.add(new Paragraph(""));
        
        // Summary Table
        float[] columnWidths = {1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f};
        Table table = new Table(columnWidths);
        
        // Headers
        table.addCell(new Cell().add(new Paragraph("Factura").setFont(boldFont)));
        table.addCell(new Cell().add(new Paragraph("Fecha").setFont(boldFont)));
        table.addCell(new Cell().add(new Paragraph("Base (€)").setFont(boldFont)));
        table.addCell(new Cell().add(new Paragraph("Impuesto (€)").setFont(boldFont)));
        table.addCell(new Cell().add(new Paragraph("Total (€)").setFont(boldFont)));
        table.addCell(new Cell().add(new Paragraph("Estado").setFont(boldFont)));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        // Data rows
        for (Factura factura : facturas) {
            table.addCell(new Cell().add(new Paragraph(factura.getNumeroFactura())));
            table.addCell(new Cell().add(new Paragraph(factura.getFechaEmision().format(formatter))));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", factura.getMonto()))));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", factura.getMontoImpuesto()))));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", factura.getTotalConImpuesto()))));
            table.addCell(new Cell().add(new Paragraph(factura.getEstado())));
        }
        
        document.add(table);
        document.add(new Paragraph(""));
        document.add(new Paragraph("Total de Facturas: " + facturas.size()).setFont(boldFont));
        document.add(new Paragraph("Documento generado automáticamente por ProyectoSaS")
            .setFont(regularFont).setFontSize(9).setTextAlignment(TextAlignment.CENTER));
        
        document.close();
        return outputStream.toByteArray();
    }
}
