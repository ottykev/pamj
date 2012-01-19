/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is the Kowari Metadata Store.
 *
 * The Initial Developer of the Original Code is Plugged In Software Pty
 * Ltd (http://www.pisoftware.com, mailto:info@pisoftware.com). Portions
 * created by Plugged In Software Pty Ltd are Copyright (C) 2001,2002
 * Plugged In Software Pty Ltd. All Rights Reserved.
 *
 * Contributor(s): N/A.
 *
 * [NOTE: The text of this Exhibit A may differ slightly from the text
 * of the notices in the Source Code files of the Original Code. You
 * should use the text of this Exhibit A rather than the text found in the
 * Original Code Source Code for Your Modifications.]
 *
 */
package org.mulgara.store.stringpool.xa;

// Java 2 standard packages
import java.net.URI;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Calendar;

// Third party packages
import org.apache.log4j.Category;
import com.hp.hpl.jena.datatypes.xsd.impl.XSDMonthType;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.datatypes.DatatypeFormatException;

// Locally written packages
import org.mulgara.query.rdf.XSD;
import org.mulgara.store.stringpool.*;
import org.mulgara.util.Constants;

/**
 * An SPObject that represents yearly periodic Gregorian calendar months.
 *
 * @created 2004-10-06
 *
 * @author Mark Ludlow
 *
 * @version $Revision$
 *
 * @modified $Date: 2005/03/11 04:15:22 $
 *
 * @maintenanceAuthor $Author: raboczi $
 *
 * @company <A href="mailto:info@PIsoftware.com">Plugged In Software</A>
 *
 * @copyright &copy; 2004 <A href="http://www.PIsoftware.com/">Plugged In
 *      Software Pty Ltd</A>
 *
 * @licence <a href="{@docRoot}/../../LICENCE">Mozilla Public License v1.1</a>
 */
public final class SPGMonthImpl extends AbstractSPTypedLiteral {

  private final static Category logger =
      Category.getInstance(SPGMonthImpl.class.getName());

  /** The date representation for the month */
  private Calendar month;

  static final int TYPE_ID = 11; // Unique ID

  /** URI for our gmonth representation */
  static final URI TYPE_URI = XSD.GMONTH_URI;

  /** Indicator as to whether we have a time zone or not */
  private boolean hasTimeZone;

  /**
   * Constructs a new GMonth representation using a calendar object representation.
   *
   * @param monthDate The gMonth object represented as an integer
   */
  SPGMonthImpl(Date monthDate) {

    // Call the super constructor
    super(TYPE_ID, TYPE_URI);

    // Initialise the calendar object
    month = Calendar.getInstance();

    // Store the month date as a calendar
    month.setTime(monthDate);
  }

  /**
   * Constructs a new GMonth representation using a calendar object representation.
   *
   * @param monthCalendar The gMonth object represented as a calendar
   */
  SPGMonthImpl(Calendar monthCalendar) {

    // Call the super constructor
    super(TYPE_ID, TYPE_URI);

    // Store the month date as a calendar
    month = monthCalendar;
  }

  /**
   *
   * Constructs a gMonth object which reads the month value from a byte buffer as
   * an integer.
   *
   * @param data The byte buffer storing the month as an integer
   */
  SPGMonthImpl(ByteBuffer data) {

    // Call the constructor using a long for the date
    this(data.getLong());
  }

  /**
   * Creates a new gMonth representation using a long value of the month
   * and creating a Date object from it.
   *
   * @param monthLong The month as a long
   */
  SPGMonthImpl(long monthLong) {

    // Use the date constructor to create a new instance
    this(new Date(monthLong));
  }

  /**
   * Constructs a new GMonth object given the lexical form of a date.
   *
   * @param lexicalForm The lexical form of the GMonth object
   * @return A new SPGMonth instance
   */
  static SPGMonthImpl newInstance(String lexicalForm) {

    // The XSD month type object we are creating
    SPGMonthImpl monthImpl = null;

    // Container for our date time object
    XSDDateTime dateTime = null;

    // Create a data type to represent a gMonth
    XSDMonthType dataType = new XSDMonthType("gMonth");

    try {

      // Create a date time object to parse out our date
      dateTime = (XSDDateTime) dataType.parseValidated(lexicalForm);
    } catch (RuntimeException ex) {

      // Since the highest level exception that can occur during parsing is the
      // runtime exception, we should capture them and report a bad lexical
      // formation
      throw new IllegalArgumentException("Invalid gMonth lexical format: " +
                                         lexicalForm);
    }

    // Jena wraps date values larger than 12, which we don't want, so we take
    // the valid date (because it parsed) and check that it is within bounds
    String dateValue = lexicalForm.substring(2, 4);

    // Parse the value to an integer (We know it is a valid number)
    int dateInt = Integer.parseInt(dateValue);

    // Check that the value is valid
    if (dateInt <= 0 || dateInt >= 13) {

      throw new IllegalArgumentException("gMonth value [" + lexicalForm +
                                         "] is not a valid month number.");
    }

    // Get the date/time object as a calendar
    Calendar calendar = dateTime.asCalendar();

    // Jena does not observe the zero based structure of the Calendar object
    // so we need to adjust for this
    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);

    if (logger.isDebugEnabled()) {

      logger.debug("Month value before calendar is: " + dateTime.getMonths());
      logger.debug("Calendar lexical string is: " +
                   calendar.get(Calendar.YEAR) + "-" +
                   calendar.get(Calendar.MONTH) + "-" +
                   calendar.get(Calendar.DAY_OF_MONTH));
      logger.debug("TimeZone of calendar is: " +
                   calendar.getTimeZone().toString());
      logger.debug("Calendar as date: " + calendar.getTime().toString());
    }

    // Create our object
    monthImpl = new SPGMonthImpl(calendar);

    if (lexicalForm.indexOf("Z") > 1 || lexicalForm.split("-").length == 6 ||
        lexicalForm.split("-").length == 4 || lexicalForm.indexOf("+") > 1) {

      // If we have a timezone then set the flag to be true
      monthImpl.setHasTimeZone(true);
    }

    return monthImpl;
  }

  /**
   * A local method to set whether we have a timezone or not.
   *
   * @param value Whether we have a time zone or not
   */
  public void setHasTimeZone(boolean value) {

    // Store whether we have a timezone or not
    hasTimeZone = value;
  }

  /**
   * Converts this gMonth object to a buffer of byte data.
   *
   * @return The byte representation of this gMonth object
   */
  public ByteBuffer getData() {

    // Create a new byte buffer that can hold a long object
    ByteBuffer data = ByteBuffer.allocate(Constants.SIZEOF_LONG);

    // Store the date as a long value
    data.putLong(month.getTimeInMillis());

    // Prepare the buffer for reading
    data.flip();

    return data;
  }

  /**
   * Create a new comparator for comparison operations.
   *
   * @return The comparator to be used for comparisons
   */
  public SPComparator getSPComparator() {

    return SPGMonthComparator.getInstance();
  }

  /**
   * Convert the gMonth representation to a lexical string as defined by XSD
   * datatypes.
   *
   * @return The lexical form of the gMonth object
   */
  public String getLexicalForm() {

    // Create the default format string
    String formatString = "'--'MM";

    if (hasTimeZone) {

      formatString += "'Z'";
    }

    // Create a formatter to parse the date
    SimpleDateFormat formatter = new SimpleDateFormat(formatString);

    // Apply the formatting
    return formatter.format(month.getTime());
  }

  /**
   * Compares this gMonth representation to another object to see if they are
   * the same values.  First the typing is checked and then the value.
   *
   * @param object The object we are comparing against
   *
   * @return Whether the gMonth value is greater than (> 0), less than (< 0), or
   *         equal to (0) this value
   */
  public int compareTo(Object object) {

    // Compare types.
    int comparison = super.compareTo(object);

    // If we have not got matching types return the value
    if (comparison != 0) {

      return comparison;
    }

    // Compare the dates lexiocally
    return getLexicalForm().compareTo(((SPGMonthImpl) object).getLexicalForm());
  }

  /**
   * Calculate the hash code for the gMonth object
   *
   * @return The hash code for the object
   */
  public int hashCode() {

    return month.hashCode();
  }

  /**
   * Determines whether the object is equal to the one passed in, in both type
   * and value.  This is different to the compareTo(Object) method in the
   * respect that it does a direct comparison, not a ranking comparison.
   *
   * @param object The object to compare this one to
   *
   * @return Whether the object is the same as this one
   */
  public boolean equals(Object object) {

    // Check for null.
    if (object == null) {

      return false;
    }

    if (object.getClass().isInstance(this)) {

      // If the object is also a gMonth object then compare the date
      return ((SPGMonthImpl) object).getLexicalForm().equals(getLexicalForm());
    } else {

      // The object is of a different type and not equal
      return false;
    }
  }

  /**
   * Implementation of an SPComparator which compares the binary representations
   * of a GMonth object.
   */
  public static class SPGMonthComparator implements SPComparator {

    /** Singleton instance of the comparator */
    private static final SPGMonthComparator INSTANCE = new SPGMonthComparator();

    /**
     * Retrieves the singleton instance of this comparator.
     *
     * @return The comparator singleton instance
     */
    public static SPGMonthComparator getInstance() {

      return INSTANCE;
    }

    /**
     * Gives the comparator an opportunity to return an ordering where only the
     * prefix of the binary representation of one or both SPObjects is available.
     * If the comparator does not support this method or if an ordering can not
     * be determined from the available data then zero (0) should be returned.
     *
     * @param d1 The first gMonth's byte buffer
     * @param d2 The second gMonth's byte buffer
     * @param d2Size The number of bytes to compare
     *
     * @return Whether the first prefix is greater than (> 0), less than (< 0),
     *         or equal to (0) the other
     */
    public int comparePrefix(ByteBuffer d1, ByteBuffer d2, int d2Size) {
      return 0;
    }

    /**
     * Compares the content of a byte buffer to the other and determines whether
     * they are equal or not.
     *
     * @param d1 The first byte buffer
     * @param d2 The second byte buffer
     * @return Whether the first buffer's content is greater than (> 0), less
     *         than (< 0), or equal to (0) the other
     */
    public int compare(ByteBuffer d1, ByteBuffer d2) {
      return compare(d1.getLong(), d2.getLong());
    }

    /**
     * Compares two longs and returns whether the first is equal to (0), greater
     * than (> 0), or less than (< 0) the second.
     *
     * @param a The first long
     * @param b The second long
     * @return Whether the first is equal to (0), greater than (> 0), or less
     *         than (< 0) the second
     */
    private static int compare(long a, long b) {

      return a == b ? 0 : (a < b ? -1 : 1);
    }
  }
}
