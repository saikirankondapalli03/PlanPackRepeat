package com.travellerapp.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.travellerapp.domain.Destination;
import com.travellerapp.domain.Itinerary;
import com.travellerapp.domain.ReportEntity;

public class PdfReportUtil {

	public static ByteArrayInputStream citiesReport(List<Itinerary> itineraries) {
		Document document = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		List<ReportEntity> report= new ArrayList<ReportEntity>();
		
		for(Itinerary it: itineraries) {
			List<Destination> destinations=it.getDestinations();
			for(Destination d: destinations) {
				ReportEntity rpt= new ReportEntity();
				rpt.setItineararyName(it.getItineraryName());
				rpt.setDestinationName(d.getDestName());
				rpt.setAddress(d.getAddress());
				rpt.setPlannedTime(d.getPlannedTime());
				rpt.setEmail(d.getEmailId());
				report.add(rpt);
			}	
		}
		
		
		try {
			PdfPTable table = new PdfPTable(5);
			table.setWidthPercentage(100 );
			table.setWidths(new int[] { 6, 6, 6, 6, 6 });

			Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

			PdfPCell hcell;
			hcell = new PdfPCell(new Phrase("ItineraryName", headFont));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setPadding(10);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("DestinationName", headFont));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setPadding(10);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Address", headFont));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setPadding(10);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("PlannedTime", headFont));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setPadding(10);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("EmailId", headFont));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setPadding(10);
			table.addCell(hcell);

			
			for (ReportEntity dest: report) {
				PdfPCell cell;

				cell = new PdfPCell(new Phrase(dest.getItineararyName()));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase(dest.getDestinationName()));
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table.addCell(cell);
				
				
				cell = new PdfPCell(new Phrase(dest.getAddress()));
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table.addCell(cell);
				
				cell = new PdfPCell(new Phrase(dest.getPlannedTime().toString()));
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table.addCell(cell);
				
				

				cell = new PdfPCell(new Phrase(dest.getEmail().toString()));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(5);
				table.addCell(cell); 
			
			}

		
			
			PdfWriter.getInstance(document, out);
			document.open();
			document.add(table);

			document.close();

		} catch (DocumentException ex) {

			Logger.getLogger(PdfReportUtil.class.getName()).log(Level.SEVERE, null, ex);
		}

		return new ByteArrayInputStream(out.toByteArray());
	}

}
