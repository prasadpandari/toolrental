import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Enum for tool types
enum ToolType {
    LADDER("Ladder", 1.99, true, true, false),
    CHAINSAW("Chainsaw", 1.49, true, false, true),
    JACKHAMMER("Jackhammer", 2.99, true, false, false);

    private final String typeName;
    private final double dailyCharge;
    private final boolean weekdayCharge;
    private final boolean weekendCharge;
    private final boolean holidayCharge;

    ToolType(String typeName, double dailyCharge, boolean weekdayCharge, boolean weekendCharge, boolean holidayCharge) {
        this.typeName = typeName;
        this.dailyCharge = dailyCharge;
        this.weekdayCharge = weekdayCharge;
        this.weekendCharge = weekendCharge;
        this.holidayCharge = holidayCharge;
    }

    public String getTypeName() {
        return typeName;
    }

    public double getDailyCharge() {
        return dailyCharge;
    }

    public boolean isWeekdayCharge() {
        return weekdayCharge;
    }

    public boolean isWeekendCharge() {
        return weekendCharge;
    }

    public boolean isHolidayCharge() {
        return holidayCharge;
    }
}

// Class to represent a tool
class Tool {
    private final String toolCode;
    private final ToolType toolType;
    private final String brand;

    public Tool(String toolCode, ToolType toolType, String brand) {
        this.toolCode = toolCode;
        this.toolType = toolType;
        this.brand = brand;
    }

    public String getToolCode() {
        return toolCode;
    }

    public ToolType getToolType() {
        return toolType;
    }

    public String getBrand() {
        return brand;
    }
}

// Class to represent a rental agreement
class RentalAgreement {
    private final String toolCode;
    private final ToolType toolType;
    private final String brand;
    private final int rentalDays;
    private final Date checkoutDate;
    private final Date dueDate;
    private final double dailyRentalCharge;
    private final int chargeDays;
    private final double preDiscountCharge;
    private final int discountPercent;
    private final double discountAmount;
    private final double finalCharge;

    public RentalAgreement(String toolCode, ToolType toolType, String brand, int rentalDays,
            Date checkoutDate, Date dueDate, double dailyRentalCharge,
            int chargeDays, double preDiscountCharge, int discountPercent,
            double discountAmount, double finalCharge) {
        this.toolCode = toolCode;
        this.toolType = toolType;
        this.brand = brand;
        this.rentalDays = rentalDays;
        this.checkoutDate = checkoutDate;
        this.dueDate = dueDate;
        this.dailyRentalCharge = dailyRentalCharge;
        this.chargeDays = chargeDays;
        this.preDiscountCharge = preDiscountCharge;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.finalCharge = finalCharge;
    }

    public void printRentalAgreement() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
        DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
        DecimalFormat percentFormat = new DecimalFormat("#%");

        System.out.println("Tool code: " + toolCode);
        System.out.println("Tool type: " + toolType.getTypeName());
        // Print other attributes similarly
        System.out.println("Final charge: " + currencyFormat.format(finalCharge));
    }
}

// Class to handle checkout process
class Checkout {
    private static final Map<String, Tool> toolInventory = new HashMap<>();

    static {
        // Initialize tool inventory
        toolInventory.put("LADW", new Tool("LADW", ToolType.LADDER, "Werner"));
        toolInventory.put("CHNS", new Tool("CHNS", ToolType.CHAINSAW, "Stihl"));
        toolInventory.put("JAKD", new Tool("JAKD", ToolType.JACKHAMMER, "DeWalt"));
        toolInventory.put("JAKR", new Tool("JAKR", ToolType.JACKHAMMER, "Ridgid"));
    }

    public static RentalAgreement checkoutTool(String toolCode, int rentalDays, int discountPercent,
            Date checkoutDate) {
        Tool tool = toolInventory.get(toolCode);
        if (tool == null) {
            throw new IllegalArgumentException("Tool with code " + toolCode + " not found.");
        }

        if (rentalDays < 1) {
            throw new IllegalArgumentException("Rental day count must be 1 or greater.");
        }

        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount percent must be between 0 and 100.");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(checkoutDate);
        calendar.add(Calendar.DAY_OF_YEAR, rentalDays);
        Date dueDate = calendar.getTime();

        // Calculate charge days based on tool type and dates
        int chargeDays = calculateChargeDays(checkoutDate, dueDate, tool.getToolType());

        // Calculate charges
        double preDiscountCharge = chargeDays * tool.getToolType().getDailyCharge();
        double discountAmount = preDiscountCharge * discountPercent / 100.0;
        double finalCharge = preDiscountCharge - discountAmount;

        return new RentalAgreement(tool.getToolCode(), tool.getToolType(), tool.getBrand(), rentalDays,
                checkoutDate, dueDate, tool.getToolType().getDailyCharge(), chargeDays,
                preDiscountCharge, discountPercent, discountAmount, finalCharge);
    }

    private static int calculateChargeDays(Date checkoutDate, Date dueDate, ToolType toolType) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(checkoutDate);

        int chargeDays = 0;
        while (calendar.getTime().before(dueDate)) {
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if ((dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY && toolType.isWeekdayCharge()) ||
                    (dayOfWeek == Calendar.SATURDAY && toolType.isWeekendCharge()) ||
                    (dayOfWeek == Calendar.SUNDAY && toolType.isWeekendCharge())) {
                chargeDays++;
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return chargeDays;
    }
}

public class ToolRental {
    public static void main(String[] args) {
        // Example usage
        String toolCode = "LADW";
        int rentalDays = 4;
        int discountPercent = 20;
        Date checkoutDate = new Date(); // Use actual checkout date here

        try {
            RentalAgreement rentalAgreement = Checkout.checkoutTool(toolCode, rentalDays, discountPercent,
                    checkoutDate);
            rentalAgreement.printRentalAgreement();
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
