package org.dhis2.commons.team

import org.dhis2.commons.date.DateUtils.YEARLY_FORMAT_EXPRESSION
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun dateToYearlyPeriod(date: Date?): String? {
    if (date ==null) return null

    return SimpleDateFormat(
        YEARLY_FORMAT_EXPRESSION,
        Locale.getDefault()
    ).format(date)
}
