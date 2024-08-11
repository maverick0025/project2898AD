# project2898AD
This is the gCalendar and gMail project.

### Current status:

Able to send emails to attendees of an event.

### Future tasks:
1. Scheduler to check the expiration of access token and call refresh access token function.
2. Add custom Exception handler to handle exceptions.
3. Implement Rate limiting
4. Implement circuit breaker and default method if breaker in action.

### Inprogress tasks:
1. Add a function to check the specific event for it's start time, end time and also end date. Manage well with end date especially.
 This function is done. But check and send email only if there are any events in the next 2 hours. I think if I implement a scheduler here it would do the job
### Tasks finished:
1. Implement get authorization code from google oauth
2. Take the url from the browser session and store it for future
3. Implement get access token and refresh token using auth code
4. Implement refresh access token functionality



