package com.inn.cafe.serviceImpl;

import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.dao.BillDao;
import com.inn.cafe.model.Bill;
import com.inn.cafe.service.BillService;
import com.inn.cafe.utils.CafeUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.Map;



@Slf4j
@Service
public class BillServiceImpl implements BillService {
    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    BillDao billDao;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("Inside generate Report");
        try
        {
            String fileName = "";
            if(validateRequestMap(requestMap)){
                if(requestMap.containsKey("isGenerate") && !(Boolean)requestMap.get("isGenerate")){
                    fileName = (String) requestMap.get("uuid");
                } else {
                    fileName = CafeUtils.getUUID();
                    requestMap.put("uuid", fileName);
                    insertBill(requestMap);
                }
            }
            String data = "Name: " + requestMap.get("name") + "\n" +
                    "Contact Number: " + requestMap.get("contactNumber") +
                    "\n" + "E-mail: " + requestMap.get("email") +
                    "\n" + "Payment Method: " + requestMap.get("paymentMethod") +
                    "\n";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(CafeConstants.STORE_LOCATION + "\\" + fileName + ".pdf"));
            document.open();
            setRectangleInPdf(document);

            Paragraph chunk = new Paragraph("Cafe Management System - Paboo Version", getFont("Header"));
            chunk.setAlignment(Element.ALIGN_CENTER);
            document.add(chunk);

            Paragraph paragraph = new Paragraph(data + "\n\n", getFont("Data"));
            document.add(paragraph);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            addTableHeader(table);


            return CafeUtils.getResponseEntity("Required data not found", HttpStatus.BAD_REQUEST);

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void addTableHeader(PdfPTable table) {
    }

    private Font getFont(String type) {
        switch(type){
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLUE);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Data":
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;
            default:
                return new Font();

        }
    }

    private void setRectangleInPdf(Document document) throws DocumentException {
        log.info("Inside setRectanglePdf");
        Rectangle rect = new Rectangle(577,825,18,15);
        rect.enableBorderSide(1);
        rect.enableBorderSide(2);
        rect.enableBorderSide(4);
        rect.enableBorderSide(8);
        rect.setBorderColor(BaseColor.BLACK);;
        rect.setBorderWidth(1);
        document.add(rect);
    }


    private boolean validateRequestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("paymentMethod") &&
                requestMap.containsKey("productDetails") &&
                requestMap.containsKey("totalAmount");
    }
    private void insertBill(Map<String, Object> requestMap) {
        try
        {
            Bill billObj = new Bill();
            billObj.setUuid((String)requestMap.get("uuid"));
            billObj.setName((String)requestMap.get("name"));
            billObj.setEmail((String)requestMap.get("email"));
            billObj.setContactNumber((String)requestMap.get("contactNumber"));
            billObj.setPaymentMethod((String)requestMap.get("paymentMethod"));
            billObj.setTotal(Integer.parseInt((String)requestMap.get("totalAmount")));
            billObj.setProductDetail((String)requestMap.get("productDetails"));
            billObj.setCreatedBy(jwtFilter.getCurrentUser());
            billDao.save(billObj);

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
