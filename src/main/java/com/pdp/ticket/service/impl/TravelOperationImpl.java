package com.pdp.ticket.service.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfXrefTable;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.pdp.ticket.enam.BotState;
import com.pdp.ticket.enam.TicketStatus;
import com.pdp.ticket.enam.TravelStatus;
import com.pdp.ticket.model.*;
import com.pdp.ticket.dto.EditingTravel;
import com.pdp.ticket.service.TravelOperation;
import com.pdp.ticket.util.KeybordHelper;
import com.pdp.ticket.util.StorageOperation;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TravelOperationImpl implements TravelOperation {
    @Override
    public SendMessage openTravel(CallbackQuery callbackQuery) {
        SendMessage editMessageText = new SendMessage();
        editMessageText.setText("Travel operation menu \uD83D\uDC47\uD83C\uDFFB");
        editMessageText.setChatId(callbackQuery.getMessage().getChatId());
        editMessageText.setReplyMarkup(getTravelOperationMenu());
        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.TRAVEL_MENU);
        return editMessageText;
    }

    private InlineKeyboardMarkup getTravelOperationMenu() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboard = KeybordHelper.createInlineKeyboard(2, 2);
        inlineKeyboard.get(0).get(0).setText("Show Travel");
        inlineKeyboard.get(0).get(0).setCallbackData("show_travel");

        inlineKeyboard.get(0).get(1).setText("Create travel");
        inlineKeyboard.get(0).get(1).setCallbackData("create_travel");

        inlineKeyboard.get(1).get(0).setText("Edit Travel");
        inlineKeyboard.get(1).get(0).setCallbackData("edit_travel");

        inlineKeyboard.get(1).get(1).setText("Delete Travel");
        inlineKeyboard.get(1).get(1).setCallbackData("delete_travel");

        List<InlineKeyboardButton> oneRow = KeybordHelper.createInlinKeyboard(1);
        oneRow.get(0).setText("Back <<");
        oneRow.get(0).setCallbackData("back_to_admin_menu");
        inlineKeyboard.add(oneRow);
        inlineKeyboardMarkup.setKeyboard(inlineKeyboard);
        return inlineKeyboardMarkup;
    }

    @Override
    public EditMessageText addFromTravel(CallbackQuery callbackQuery) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setChatId(callbackQuery.getMessage().getChatId().toString());
        if (getDestinationsButton(true, "") == null) {
            editMessageText.setText("Destination should be more than 2");
            List<InlineKeyboardButton> oneRow = KeybordHelper.createInlinKeyboard(1);
            oneRow.get(0).setText("Back <<");
            oneRow.get(0).setCallbackData("back_to_admin_menu");
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(List.of(oneRow));
            StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.ADMIN_MENU);
            editMessageText.setReplyMarkup(inlineKeyboardMarkup);
            return editMessageText;
        }
        editMessageText.setReplyMarkup(getDestinationsButton(true, ""));
        editMessageText.setText("Choose 'From' \uD83D\uDC47\uD83C\uDFFB");
        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.TRAVEL_ADD_FROM);
        return editMessageText;
    }

    @Override
    public EditMessageText addToTravel(CallbackQuery callbackQuery) {
        String fromId = callbackQuery.getData().split("_")[2];
        EditingTravel editingTravel = new EditingTravel();
        editingTravel.setId(callbackQuery.getMessage().getChatId().toString());
        Travel travel = new Travel();
        travel.setId(UUID.randomUUID());
        Destination destination = StorageOperation.getDestination().stream()
                .filter(des -> des.getId().toString().equals(fromId)).findFirst().orElse(null);
        travel.setFrom(destination);
        editingTravel.setTravel(travel);
        StorageOperation.writeEditingTravel(List.of(editingTravel));

        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.TRAVEL_ADD_TO);
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setChatId(callbackQuery.getMessage().getChatId().toString());
        editMessageText.setReplyMarkup(getDestinationsButton(false, fromId));
        editMessageText.setText("Choose 'To' \uD83D\uDC47\uD83C\uDFFB ");
        return editMessageText;
    }

    @Override
    public EditMessageText addDepartureTimeTravel(CallbackQuery callbackQuery) {
        String toID = callbackQuery.getData().split("_")[2];
        Destination destination = StorageOperation.getDestination().stream()
                .filter(des -> des.getId().toString().equals(toID)).findFirst().orElse(null);

        List<EditingTravel> editingTravel1 = StorageOperation.getEditingTravel();
        EditingTravel editingTravel = editingTravel1.stream()
                .filter(t -> t.getId().equals(callbackQuery.getMessage().getChatId().toString())).findFirst().orElse(new EditingTravel());
        editingTravel.getTravel().setTo(destination);
        StorageOperation.writeEditingTravel(editingTravel1);
        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.TRAVEL_ADD_DEPARTURE_TIME);
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setChatId(callbackQuery.getMessage().getChatId());
        editMessageText.setText(" DepartureTime ni kiriting --> Example: 22.01.2023 16:16");
        return editMessageText;
    }

    @Override
    public SendMessage addArrivalTimeTravel(Message message) {
        SendMessage editMessageText = new SendMessage();
        editMessageText.setChatId(message.getChatId());
        String departureTime = message.getText();
        if (!departureTime.matches("^[0-9]{2}[.][0-9]{2}[.][0-9]{4} [0-9]{2}:[0-9]{2}$")) {
            editMessageText.setText(" DepartureTime ni noto`g`ri formatda kiritdingiz!");
            editMessageText.setReplyMarkup(getTravelOperationMenu());
            StorageOperation.updateUserState(message.getChatId().toString(), BotState.TRAVEL_MENU);
            return editMessageText;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(departureTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        List<EditingTravel> editingTravels = StorageOperation.getEditingTravel();
        EditingTravel editingTravel = editingTravels.stream()
                .filter(edit -> edit.getId().equals(message.getChatId().toString())).findFirst().orElse(new EditingTravel());
        editingTravel.getTravel().setDepartureTime(localDateTime);
        StorageOperation.writeEditingTravel(editingTravels);
        StorageOperation.updateUserState(message.getChatId().toString(), BotState.TRAVEL_ADD_ARRIVAL_TIME);
        editMessageText.setText(" ArrivalTime ni togri namunadagidek kiriting -->\n  Example: 22.01.2023 16:16 ");
        return editMessageText;
    }

    @Override
    public SendMessage addBusTravel(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        String arrivalTime = message.getText();
        if (!arrivalTime.matches("^[0-9]{2}[.][0-9]{2}[.][0-9]{4} [0-9]{2}:[0-9]{2}$")) {
            sendMessage.setText(" ArrivalTime ni noto`g`ri kiritdingiz! ");
            sendMessage.setReplyMarkup(getTravelOperationMenu());
            StorageOperation.updateUserState(message.getChatId().toString(), BotState.TRAVEL_MENU);
            return sendMessage;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(arrivalTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        List<EditingTravel> editingTravels = StorageOperation.getEditingTravel();
        EditingTravel editingTravel = editingTravels.stream()
                .filter(edit -> edit.getId().equals(message.getChatId().toString())).findFirst().orElse(new EditingTravel());
        editingTravel.getTravel().setArrivalTime(localDateTime);
        StorageOperation.writeEditingTravel(editingTravels);

        List<Bus> buses = StorageOperation.getBuses().stream().filter(Bus::isActive).toList();
        if (buses.size() == 0) {
            sendMessage.setText(" Bus list is empty ");
            StorageOperation.updateUserState(message.getChatId().toString(), BotState.TRAVEL_MENU);
            sendMessage.setReplyMarkup(getTravelOperationMenu());
            return sendMessage;
        }
        sendMessage.setText(" Choose Bus \uD83D\uDC47\uD83C\uDFFB\n " + showBuses(buses));
        sendMessage.setReplyMarkup(showBusButton(buses));
        StorageOperation.updateUserState(message.getChatId().toString(), BotState.TRAVEL_ADD_BUS);
        return sendMessage;
    }

    @Override
    public SendMessage addPriceForPerSeat(CallbackQuery callbackQuery) {
        String busId = callbackQuery.getData().split("_")[2];
        List<Bus> buses = StorageOperation.getBuses();
        Bus bus1 = buses.stream().filter(bus -> bus.getId().toString().equals(busId)).findFirst().orElse(new Bus());
        List<EditingTravel> editingTravel = StorageOperation.getEditingTravel();
        EditingTravel editingTravel2 = editingTravel.stream().filter(editingTravel1 -> editingTravel1.getId().equals(callbackQuery.getMessage().getChatId().toString())).findFirst().orElse(new EditingTravel());
        editingTravel2.getTravel().setBus(bus1);
        StorageOperation.writeEditingTravel(editingTravel);
        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.TRAVEL_ADD_PRICE_FOR_PER_SEAT);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("enter price for per seat:");
        sendMessage.setChatId(callbackQuery.getMessage().getChatId());
        return sendMessage;
    }

    @Override
    public SendMessage addCreatedTimeTravel(Message message) {
        SendMessage sendMessage = new SendMessage();
        String messageText = message.getText();
        List<EditingTravel> editingTravel = StorageOperation.getEditingTravel();
        EditingTravel editingTravel2 = editingTravel.stream().filter(editingTravel1 -> editingTravel1.getId().equals(message.getChatId().toString())).findFirst().orElse(new EditingTravel());
        Travel travel = editingTravel2.getTravel();
        travel.setPriceForPerSeat(BigDecimal.valueOf(Long.parseLong(messageText)));
        travel.setStatus(TravelStatus.NEW);
        travel.setCreatedTime(LocalDateTime.now());
        StorageOperation.writeEditingTravel(new ArrayList<>());
        StorageOperation.writeTravel(travel);
        StorageOperation.updateUserState(message.getChatId().toString(), BotState.TRAVEL_MENU);
        Bus bus = travel.getBus();
        List<Ticket> tickets = new ArrayList<>();
        int numberOfSeats = bus.getNumberOfSeats();
        for (int i = 0; i < numberOfSeats; i++) {
            Ticket ticket = new Ticket();
            ticket.setId(UUID.randomUUID());
            ticket.setPrice(travel.getPriceForPerSeat());
            ticket.setTravel(travel);
            ticket.setStatus(TicketStatus.AVAILABLE);
            ticket.setSeatNumber(i + 1);
            tickets.add(ticket);
        }
        List<Ticket> allTickets = StorageOperation.getTickets();
        allTickets.addAll(tickets);
        StorageOperation.writeTickets(allTickets);
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("Successfully added");
        sendMessage.setReplyMarkup(getTravelOperationMenu());
        return sendMessage;
    }

    @Override
    public SendMessage showTravels(CallbackQuery callbackQuery) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(callbackQuery.getMessage().getChatId());
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboard = KeybordHelper.createInlineKeyboard(2, 2);
        inlineKeyboard.get(0).get(0).setText("by Chat");
        inlineKeyboard.get(0).get(0).setCallbackData("by_chat_show_travel");
        inlineKeyboard.get(0).get(1).setText("by Word.docx");
        inlineKeyboard.get(0).get(1).setCallbackData("by_word_show_travel");
        inlineKeyboard.get(1).get(0).setText("by Pdf.pdf");
        inlineKeyboard.get(1).get(0).setCallbackData("by_pdf_show_travel");
        inlineKeyboard.get(1).get(1).setText("by Excel");
        inlineKeyboard.get(1).get(1).setCallbackData("by_excel_show_travel");
        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.TRAVEL_SHOW_MENU);
        inlineKeyboardMarkup.setKeyboard(inlineKeyboard);
        sendMessage.setText(" choose menu \uD83D\uDC47\uD83C\uDFFB ");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }


    @Override
    public SendMessage showTravelByBot(CallbackQuery callbackQuery) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(callbackQuery.getMessage().getChatId());
        List<Travel> travels = StorageOperation.getTravels();
        if (travels.size() == 0) {
            sendMessage.setText(" Travel list is empty ");
            sendMessage.setReplyMarkup(backToTravelMenu());
            StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.TRAVEL_MENU);
            return sendMessage;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Travel travel : travels) {
            stringBuilder.append("From: ").append(travel.getFrom().getName()).append(" --> To: ").append(travel.getTo().getName())
                    .append("\nDepartureTime: ").append(travel.getDepartureTime()).append(" --> ArrivalTime: ").append(travel.getArrivalTime())
                    .append("\nAbout bus -> name: ").append(travel.getBus().getName()).append(", the number of Seats: ").append(travel.getBus().getNumberOfSeats())
                    .append("\nCreatedTime: ").append(travel.getCreatedTime()).append(", Status: ").append(travel.getStatus()).append("\n----------------------------------\n");
        }
        sendMessage.setText(stringBuilder.toString());
        sendMessage.setReplyMarkup(backToTravelMenu());
        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.TRAVEL_MENU);
        return sendMessage;
    }

    private InlineKeyboardMarkup backToTravelMenu(){
        List<InlineKeyboardButton> oneRow = KeybordHelper.createInlinKeyboard(1);
        oneRow.get(0).setText("Back <<");
        oneRow.get(0).setCallbackData("back_to_travel_menu");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(List.of(oneRow));
        return inlineKeyboardMarkup;
    }

    @Override
    public SendDocument showTravelByWord(CallbackQuery callbackQuery) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(callbackQuery.getMessage().getChatId());
        List<Travel> travels = StorageOperation.getTravels();
        if (travels.size() == 0) {
            sendDocument.setCaption(" List is empty ");
            StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.TRAVEL_MENU);
            sendDocument.setReplyMarkup(backToTravelMenu());
            return sendDocument;
        }
        int size = travels.size();
        File file = new File("src/main/resources/files/travelList.docx");
        try (XWPFDocument document = new XWPFDocument();
             FileOutputStream outputStream = new FileOutputStream(file)) {
            XWPFTable table = document.createTable((size + 1), 8);
            table.setTableAlignment(TableRowAlign.LEFT);
            table.setWidth(6000);
            XWPFTableRow row = table.getRow(0);
            row.getCell(0).setText("From");
            row.getCell(1).setText("To");
            row.getCell(2).setText("DepartureTime");
            row.getCell(3).setText("ArrivalTime");
            row.getCell(4).setText("Bus's name");
            row.getCell(5).setText("the number of Seats of Bus");
            row.getCell(6).setText("CreatedTime");
            row.getCell(7).setText("Status");
            for (int i = 0; i < size; i++) {
                Travel travel = travels.get(i);
                XWPFTableRow row1 = table.getRow((i + 1));
                row1.getCell(0).setText(travel.getFrom().getName());
                row1.getCell(1).setText(travel.getTo().getName());
                row1.getCell(2).setText(String.valueOf(travel.getDepartureTime()));
                row1.getCell(3).setText(String.valueOf(travel.getArrivalTime()));
                row1.getCell(4).setText(travel.getBus().getName());
                row1.getCell(5).setText(String.valueOf(travel.getBus().getNumberOfSeats()));
                row1.getCell(6).setText(String.valueOf(travel.getCreatedTime()));
                row1.getCell(7).setText(String.valueOf(travel.getStatus()));
            }
            document.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputFile inputFile = new InputFile(file);
        sendDocument.setDocument(inputFile);
        sendDocument.setCaption("Travel list");
        sendDocument.setReplyMarkup(backToTravelMenu());
        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.TRAVEL_MENU);
        return sendDocument;
    }

    @Override
    public SendDocument showTravelByPdf(CallbackQuery callbackQuery) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(callbackQuery.getMessage().getChatId());
        List<Travel> travels = StorageOperation.getTravels();
        if (travels.size() == 0) {
            sendDocument.setCaption(" Travel list is empty ");
            sendDocument.setReplyMarkup(backToTravelMenu());
            StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.TRAVEL_MENU);
            return sendDocument;
        }
        File file = new File("src/main/resources/files/travelList2.pdf");
        try (PdfWriter pdfWriter = new PdfWriter(file);
             PdfDocument pdfDocument = new PdfDocument(pdfWriter);
             Document document = new Document(pdfDocument)
        ) {
            Paragraph paragraph = new Paragraph();
            paragraph.setTextAlignment(TextAlignment.CENTER);
            paragraph.add(" Travel list");
            document.add(paragraph);
            Table table = new Table(8);
            table.addCell("From");
            table.addCell("To");
            table.addCell("DepartureTime");
            table.addCell("ArrivalTime");
            table.addCell("Bus's name");
            table.addCell("the number of Seats of Bus");
            table.addCell("CreatedTime");
            table.addCell("Status");
            for (Travel travel : travels) {
                table.addCell(travel.getFrom().getName());
                table.addCell(travel.getTo().getName());
                table.addCell(String.valueOf(travel.getDepartureTime()));
                table.addCell(String.valueOf(travel.getArrivalTime()));
                table.addCell(travel.getBus().getName());
                table.addCell(String.valueOf(travel.getBus().getNumberOfSeats()));
                table.addCell(String.valueOf(travel.getCreatedTime()));
                table.addCell(String.valueOf(travel.getStatus()));
            }
            document.add(table);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputFile inputFile = new InputFile(file);
        sendDocument.setDocument(inputFile);
        sendDocument.setCaption(" Travel list");
        sendDocument.setReplyMarkup(backToTravelMenu());
        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.TRAVEL_MENU);
        return sendDocument;
    }

    @Override
    public SendDocument showTravelByExcel(CallbackQuery callbackQuery) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(callbackQuery.getMessage().getChatId());
        List<Travel> travels = StorageOperation.getTravels();
        if (travels.size() == 0) {
            sendDocument.setCaption(" Travel list is empty ");
            sendDocument.setReplyMarkup(backToTravelMenu());
            StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.TRAVEL_MENU);
            return sendDocument;
        }
        File file = new File("src/main/resources/files/travelList3.xlsx");
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             OutputStream outputStream = new FileOutputStream(file)
        ) {
            XSSFSheet sheet = workbook.createSheet("TravelList");
            XSSFRow row = sheet.createRow(0);
            row.createCell(0).setCellValue("From");
            row.createCell(1).setCellValue("To");
            row.createCell(2).setCellValue("DepartureTime");
            row.createCell(3).setCellValue("ArrivalTime");
            row.createCell(4).setCellValue("Bus's name");
            row.createCell(5).setCellValue("the number of Seats of Bus");
            row.createCell(6).setCellValue("CreatedTime");
            row.createCell(7).setCellValue("Status");
            int rowNumber = 1;
            for (Travel travel : travels) {
                row = sheet.createRow(rowNumber);
                row.createCell(0).setCellValue(travel.getFrom().getName());
                row.createCell(1).setCellValue(travel.getTo().getName());
                row.createCell(2).setCellValue(String.valueOf(travel.getDepartureTime()));
                row.createCell(3).setCellValue(String.valueOf(travel.getArrivalTime()));
                row.createCell(4).setCellValue(travel.getBus().getName());
                row.createCell(5).setCellValue(String.valueOf(travel.getBus().getNumberOfSeats()));
                row.createCell(6).setCellValue(String.valueOf(travel.getCreatedTime()));
                row.createCell(7).setCellValue(String.valueOf(travel.getStatus()));
                rowNumber++;
            }
            for (int i = 0; i < 8; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputFile inputFile = new InputFile(file);
        sendDocument.setDocument(inputFile);
        sendDocument.setCaption(" Travel list ");
        sendDocument.setReplyMarkup(backToTravelMenu());
        StorageOperation.updateUserState(callbackQuery.getMessage().getChatId().toString(), BotState.TRAVEL_MENU);
        return sendDocument;
    }

    private InlineKeyboardMarkup getDestinationsButton(boolean isFrom, String fromId) {
        List<Destination> destination = StorageOperation.getDestination();
        if (destination.size() < 2) {
            return null;
        }
        if (!isFrom) {
            destination = destination.stream().filter(destination1 -> !destination1.getId().toString().equals(fromId)).toList();
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        int remainder = destination.size() % 3;
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0; i < destination.size() - remainder; i++) {
            if (i % 3 == 2) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(destination.get(i).getName());
                button.setCallbackData("from_to_" + destination.get(i).getId());
                row.add(button);
                buttons.add(row);
                row = new ArrayList<>();
            } else {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(destination.get(i).getName());
                button.setCallbackData("from_to_" + destination.get(i).getId());
                row.add(button);
            }
        }
        if (remainder > 0) {
            for (int i = destination.size() - remainder; i < destination.size(); i++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(destination.get(i).getName());
                button.setCallbackData("from_to_" + destination.get(i).getId());
                row.add(button);
            }
            buttons.add(row);
        }
        inlineKeyboardMarkup.setKeyboard(buttons);
        return inlineKeyboardMarkup;
    }


    private String showBuses(List<Bus> buses) {
        StringBuilder busString = new StringBuilder();
        for (int i = 0; i < buses.size(); i++) {
            busString.append(i + 1).append(". Name: ")
                    .append(buses.get(i).getName()).append("\nnumber: ")
                    .append(buses.get(i).getNumber()).append("\nnumber of seats: ")
                    .append(buses.get(i).getNumberOfSeats()).append("\n--------------------------------------------------\n");
        }
        return busString.toString();
    }

    private InlineKeyboardMarkup showBusButton(List<Bus> buses) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        int n = buses.size() % 4;
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0; i < buses.size() - n; i++) {
            if (i % 4 == 3) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(String.valueOf((i + 1)));
                button.setCallbackData("travel_bus_" + buses.get(i).getId());
                row.add(button);
                buttons.add(row);
                row = new ArrayList<>();
            } else {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(String.valueOf((i + 1)));
                button.setCallbackData("travel_bus_" + buses.get(i).getId());
                row.add(button);
            }
        }
        if (n > 0) {
            for (int i = buses.size() - n; i < buses.size(); i++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(String.valueOf((i + 1)));
                button.setCallbackData("travel_bus_" + buses.get(i).getId());
                row.add(button);
            }
            buttons.add(row);
        }
        inlineKeyboardMarkup.setKeyboard(buttons);
        return inlineKeyboardMarkup;
    }

    private static final TravelOperationImpl travelOperationImpl = new TravelOperationImpl();

    public static TravelOperationImpl getTravelOperationImpl() {
        return travelOperationImpl;
    }

}
