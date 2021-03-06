#include <time.h>

#include "date_utils.hpp"

using std::string;

using namespace mesos::internal;


// Static fields in DateUtils
bool DateUtils::useMockDate = false;
string DateUtils::mockDate = "";


// Get the current date in the format used for Mesos IDs (YYYYMMDDhhmm).
string DateUtils::currentDate()
{
  if (useMockDate) {
    return mockDate;
  } else {
    time_t rawtime;
    struct tm* timeinfo;
    time(&rawtime);
    timeinfo = localtime(&rawtime);
    char date[32];
    strftime(date, sizeof(date), "%Y%m%d%H%M", timeinfo);
    return date;
  }
}


// Unit test utility method that makes this class return a fixed string
// as the date instead of looking up the current time.
void DateUtils::setMockDate(string date)
{
  useMockDate = true;
  mockDate = date;
}


// Disable usage of the mock date set through setMockDate.
void DateUtils::clearMockDate()
{
  useMockDate = false;
  mockDate = "";
}
