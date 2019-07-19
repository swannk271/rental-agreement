import junit.framework.TestCase;
import org.junit.Test;
import java.time.LocalDate;


public class RentalAgreementTest extends TestCase {
    String toolcode;
    LocalDate checkoutdate;
    int rentaldays;
    int discount;

    @Test
    public void testCheckout1(){
        toolcode = "JAKR";
        checkoutdate = LocalDate.of(2015,9,3);
        rentaldays = 5;
        discount = 101;
        boolean exceptionThrown = false;
        try {
            RentalAgreement.checkout(toolcode, rentaldays, discount, checkoutdate);
        }
        catch(RuntimeException e) {
            assertEquals( "Discount error, must be within the range of 0-100%", e.getMessage() );
        }
    }

    @Test
    public void testCheckout2() {
        toolcode = "LADW";
        checkoutdate = LocalDate.of(2020,7,2);
        rentaldays = 3;
        discount = 10;
        RentalAgreement rental = RentalAgreement.checkout(toolcode,rentaldays,discount,checkoutdate);
        assertEquals(rental.dueDate,LocalDate.of(2020,7,5));
        assertEquals(rental.chargePerDay,1.99f);
        assertEquals(rental.daysCharged,2);
        assertEquals(rental.beforeDiscount,3.98f);
        assertEquals(rental.discountPercent,10);
        assertEquals(rental.discountAmount,0.40f);
        assertEquals(rental.finalCharge,3.58f);
    }

    @Test
    public void testCheckout3() {
        toolcode = "CHNS";
        checkoutdate = LocalDate.of(2015,7,2);
        rentaldays = 5;
        discount = 25;
        RentalAgreement rental = RentalAgreement.checkout(toolcode,rentaldays,discount,checkoutdate);
        assertEquals(rental.dueDate,LocalDate.of(2015,7,7));
        assertEquals(rental.chargePerDay,1.49f);
        assertEquals(rental.daysCharged, 3);
        assertEquals(rental.beforeDiscount, 4.47f);
        assertEquals(rental.discountPercent,25);
        assertEquals(rental.discountAmount,1.12f);
        assertEquals(rental.finalCharge,3.35f);
    }

    @Test
    public void testCheckout4() {
        toolcode = "JAKD";
        checkoutdate = LocalDate.of(2015,9,3);
        rentaldays = 6;
        discount = 0;
        RentalAgreement rental = RentalAgreement.checkout(toolcode,rentaldays,discount,checkoutdate);
        assertEquals(rental.dueDate,LocalDate.of(2015,9,9));
        assertEquals(rental.chargePerDay,2.99f);
        assertEquals(rental.daysCharged,3);
        assertEquals(rental.beforeDiscount,8.97f);
        assertEquals(rental.discountPercent,0);
        assertEquals(rental.discountAmount,0.0f);
        assertEquals(rental.finalCharge,8.97f);
    }

    @Test
    public void testCheckout5() {
        toolcode = "JAKR";
        checkoutdate = LocalDate.of(2015,7,2);
        rentaldays = 9;
        discount = 0;
        RentalAgreement rental = RentalAgreement.checkout(toolcode,rentaldays,discount,checkoutdate);
        assertEquals(rental.dueDate,LocalDate.of(2015,7,11));
        assertEquals(rental.chargePerDay,2.99f);
        assertEquals(rental.daysCharged,5);
        assertEquals(rental.beforeDiscount,14.95f);
        assertEquals(rental.discountPercent,0);
        assertEquals(rental.discountAmount,0.0f);
        assertEquals(rental.finalCharge,14.95f);
    }

    @Test
    public void testCheckout6() {
        toolcode = "JAKR";
        checkoutdate = LocalDate.of(2020,7,2);
        rentaldays = 4;
        discount = 50;
        RentalAgreement rental = RentalAgreement.checkout(toolcode,rentaldays,discount,checkoutdate);
        assertEquals(rental.dueDate,LocalDate.of(2020,7,6));
        assertEquals(rental.chargePerDay,2.99f);
        assertEquals(rental.daysCharged,1);
        assertEquals(rental.beforeDiscount,2.99f);
        assertEquals(rental.discountPercent,50);
        assertEquals(rental.discountAmount,1.50f);
        assertEquals(rental.finalCharge,1.49f);
    }
}