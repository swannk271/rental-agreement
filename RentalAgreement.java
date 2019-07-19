import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class RentalAgreement {
    String toolCode;
    String toolType;
    String toolBrand;
    int daysRented;
    LocalDate checkoutDate;
    LocalDate dueDate;
    float chargePerDay;
    int daysCharged;
    float beforeDiscount;
    int discountPercent;
    float discountAmount;
    float finalCharge;
    Tool tool;

    RentalAgreement(String tool_code, int numDays, int percentDiscount, LocalDate dateCheckedOut, Tool tool_){
        toolCode = tool_code;
        daysRented = numDays;
        discountPercent = percentDiscount;
        checkoutDate = dateCheckedOut;
        dueDate = dateCheckedOut.plusDays(numDays);
        toolType = tool_.toolType;
        toolBrand = tool_.brand;
        chargePerDay = tool_.dailyCharge;
        tool = tool_;
    }

    public void printCheckout(){
        System.out.println( "Tool code: " + toolCode );
        System.out.println( "Tool type: " + toolType );
        System.out.println( "Tool brand: " + toolBrand );
        System.out.println( "Rental days: " + daysRented );
        System.out.println( "Checkout date: " + checkoutDate.format( DateTimeFormatter.ofLocalizedDate( FormatStyle.SHORT )));
        System.out.println( "Due date: " + dueDate.format( DateTimeFormatter.ofLocalizedDate( FormatStyle.SHORT )));
        System.out.println( "Daily rental charge: $" + chargePerDay );
        System.out.println( "Charge days: " + daysCharged );
        System.out.printf( "Pre-discount charge: $%.2f \n", beforeDiscount );
        System.out.println( "Discount percent: " + discountPercent + "%" );
        System.out.printf( "Discount amount: $%.2f \n", discountAmount );
        System.out.printf( "Final Charge: $%.2f \n", finalCharge );
    }

    // returns date of Labor day
    public LocalDate findLaborDay(){
        LocalDate laborDay = LocalDate.of( checkoutDate.getYear(),9,1 );
        // if Labor day isn't 1st of the month then get value of day (Mon=1, Tues=2, ..)
        // then subtract value from 9 to find first Monday of the month
        if ( laborDay.getDayOfWeek().getValue() != 1 )
            laborDay = laborDay.plusDays( 9 - laborDay.getDayOfWeek().getValue() );
        return laborDay;
    }

    // returns date 4th of July is observed on (Monday if 4th is Sunday && Friday if 4th on Saturday)
    public LocalDate findObservanceDay(){
        LocalDate julyFourth = LocalDate.of(checkoutDate.getYear(),7,4);
        if ( julyFourth.getDayOfWeek() ==  DayOfWeek.SATURDAY )
            julyFourth = julyFourth.minusDays(1);
        else if ( julyFourth.getDayOfWeek() == DayOfWeek.SUNDAY )
            julyFourth = julyFourth.plusDays(1);
        return julyFourth;
    }

    // returns number of holidays within rental period
    public int numHolidays(LocalDate dateDue){
        int holidayCount = 0;
        LocalDate julyFourth = findObservanceDay();
        LocalDate laborDay = findLaborDay();

        // check if rented for over a year
        if ( daysRented >= 365 ) {
            // add two holidays for every year
            holidayCount += 2 * ( daysRented / 365 );
            // subtract whole year(s) to evaluate whether any other days need to be added to holiday count
            dateDue = dateDue.minusYears( daysRented / 365 );
            // wonderful opportunity for recursion here but was worried it would cost me the job :P
        }
        // check if July 4th is within rental period
        if (checkoutDate.isBefore(julyFourth) && dueDate.isAfter(julyFourth))
            holidayCount++;

        // check if Labor day falls within the rental period
        if ( checkoutDate.isBefore(laborDay) && dueDate.isAfter(laborDay))
                holidayCount++;

        return holidayCount;
    }

    // returns number of weekend days within rental period
    public int numWeekendDays(LocalDate currentDay, int numDays){
        int weekendDayCount = 0;
        // if rented for over a week add two days for each whole week
        // then find remainder for numDays and move startDay in order to evaluate partial week left
        if ( numDays >= 7 ){
            weekendDayCount += ( 2 * ( numDays / 7 ));
            currentDay = currentDay.plusWeeks( numDays / 7 );
            numDays %= 7;
        }
        // check if any remaining days fall on the weekend
        // (Mon=1 and Fri=5 so if day + numDays <= 5 then no weekend days remain)
        if ( currentDay.getDayOfWeek().getValue() + numDays > 5 ){
            // go through remaining days and increment if weekend day (day>5)
            for ( int i = 0; i <= numDays; i++ ){
                // if current day is Saturday or Sunday
                if ( currentDay.getDayOfWeek().getValue() > 5 )
                    weekendDayCount++;
                currentDay = currentDay.plusDays(1);
            }
        }
        return weekendDayCount;
    }

    // calculate and modify beforeDiscount, discountAmount, and finalCharge
    public void calculateTotal(int daysCharged){
        // uses BigDecimal and setScale to format amount to two decimal places then converts to a float
        beforeDiscount = new BigDecimal(daysCharged * chargePerDay).setScale(2, RoundingMode.HALF_DOWN).floatValue();
        discountAmount = new BigDecimal(discountPercent * 0.01f * beforeDiscount).setScale(2, RoundingMode.HALF_DOWN).floatValue();
        finalCharge = beforeDiscount - discountAmount;
    }

    // returns a RentalAgreement instance
    static public RentalAgreement checkout(String tool_code, int numDays, int percentDiscount, LocalDate dateCheckedOut){
        // throw exception if rental period is less than a day or if percentDiscount is out of bounds
        if ( numDays < 1 )
            throw new RuntimeException("Item must be rented for at least one day to be checked out");
        if ( percentDiscount > 100 || percentDiscount < 0 )
            throw new RuntimeException("Discount error, must be within the range of 0-100%");

        // create Tool and RentalAgreement instances
        Tool tool = new Tool(tool_code);
        RentalAgreement rental = new RentalAgreement(tool_code,numDays,percentDiscount,dateCheckedOut,tool);

        // start with number of days in rental period
        rental.daysCharged = numDays;

        // if no charge on holidays then subtract them from daysCharged (if any fall within rental period)
        if (!tool.holidayCharge)
            rental.daysCharged -= rental.numHolidays(rental.dueDate);

        // if no charge on weekends then subtract them from daysCharged (if they fall within rental period)
        if (!tool.weekendCharge)
            rental.daysCharged -= rental.numWeekendDays(dateCheckedOut, numDays);

        rental.calculateTotal(rental.daysCharged);
        rental.printCheckout();
        return rental;
    }
}


class Tool {
    String toolCode;
    String brand;
    String toolType;
    float dailyCharge;
    boolean weekendCharge;
    boolean holidayCharge;

    Tool(String code){
        toolCode = code;
        // sets variables for tool based on code
        switch (code) {
            case "LADW":
                dailyCharge = 1.99f;
                weekendCharge = true;
                holidayCharge = false;
                toolType = "Ladder";
                brand = "Werner";
                break;
            case "CHNS":
                dailyCharge = 1.49f;
                weekendCharge = false;
                holidayCharge = true;
                toolType = "Chainsaw";
                brand = "Stihl";
                break;
            case "JAKR":
                dailyCharge = 2.99f;
                weekendCharge = false;
                holidayCharge = false;
                toolType = "Jackhammer";
                brand = "Ridgid";
                break;
            case "JAKD":
                dailyCharge = 2.99f;
                weekendCharge = false;
                holidayCharge = false;
                toolType = "Jackhammer";
                brand = "DeWalt";
                break;
            default:
                break;
        }
    }
}

