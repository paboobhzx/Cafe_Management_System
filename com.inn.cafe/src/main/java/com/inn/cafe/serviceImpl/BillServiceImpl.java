package com.inn.cafe.serviceImpl;

import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.dao.BillDao;
import com.inn.cafe.model.Bill;
import com.inn.cafe.service.BillService;
import com.inn.cafe.utils.CafeUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;


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
            Boolean validData = validateRequestMap(requestMap);
            if(validData){
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

            JSONArray jsonArray =
                    CafeUtils.getJsonArrayFromString((String)requestMap.get("ProductDetails"));
            for(int i=0; i < jsonArray.length(); i++){
                addRows(table, CafeUtils.getMapFromJson(jsonArray.getString(i)));
            }
            document.add(table);
            Paragraph footer = new Paragraph("Total: " + requestMap.get("totalAmount") + "\n" +
                    "Thank you for visiting", getFont("Data"));
            document.add(footer);
            document.close();
            return new ResponseEntity<>("{\"uuid\":\"" + fileName + "\"}", HttpStatus.OK);

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private void addRows(PdfPTable table, Map<String, Object> data) {
        log.info("Inside addRows");
        table.addCell((String)data.get("name"));
        table.addCell((String)data.get("category"));
        table.addCell((String)data.get("quantity"));
        table.addCell(Double.toString((Double)data.get("price")));
        table.addCell(Double.toString((Double)data.get("total")));
    }

    private void addTableHeader(PdfPTable table) {
        log.info("Inside addTableHeader");
        Stream.of("Name", "Category", "Quantity", "Price", "Sub Total")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    header.setBackgroundColor(BaseColor.YELLOW);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });
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
        rect.setBorderColor(BaseColor.BLACK);
        rect.setBorderWidth(1);
        document.add(rect);
    }


    private boolean validateRequestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("paymentMethod") &&
                requestMap.containsKey("ProductDetails") &&
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
            billObj.setProductDetail((String)requestMap.get("ProductDetails"));
            billObj.setCreatedBy(jwtFilter.getCurrentUser());
            billDao.save(billObj);

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    @Override
    public ResponseEntity<List<Bill>> getBills() {
        List<Bill> listObj = new ArrayList<>();
        if(jwtFilter.isAdmin()){
            listObj = billDao.getAllBills();
        }else{
            listObj = billDao.getBillByUserName(jwtFilter.getCurrentUser());
        }
        return new ResponseEntity<>(listObj, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        log.info("Inside getPdf");
        try
        {
            byte[] byteArray = new byte[0];
            if(!requestMap.containsKey("uuid") && validateRequestMap(requestMap)){
                return new ResponseEntity<>(byteArray, HttpStatus.BAD_REQUEST);
            }

            String filePath = CafeConstants.STORE_LOCATION + "\\"+
                    (String)requestMap.get("uuid")+ ".pdf";

            if(CafeUtils.fileExists(filePath)){
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            }
            else {
                requestMap.put("isGenerated", false);
                generateReport(requestMap);
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);

            }


        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }


    private byte[] getByteArray(String filePath) throws Exception{
        File initialFile = new File(filePath);
        InputStream targetStream = new FileInputStream(initialFile);
        byte[] byteArray = IOUtils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;
    }

    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        try
        {
            Optional optionalObj = billDao.findById(id);
            if(!optionalObj.isEmpty()){
                billDao.deleteById(id);
                return CafeUtils.getResponseEntity("Bill deleted", HttpStatus.OK);
            }else {
                return CafeUtils.getResponseEntity("BIll id doesn't exists", HttpStatus.OK);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
