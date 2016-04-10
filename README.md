#How to proceed
Malnati suggests to split the work on the repository by functionalities rather than activities. This is because it is unlikely to have activities that are totally independent from others and, especially, from data structures. Also it's better to split the design (layouts) from the programming.

The approach each one of us should take once the tasks are assigned is the following: you create a branch representing the functionality you are implementing. You keep working on YOUR branch and once it is completed you inform the *"team leader"*. The *team leader*, other than working on his branch, is in charge also of merging your branch with the master. You **MUST NOT** merge your branch by yourself.

What we now need is ***nominate a team leader*** to perform the merging between the branches and the master.

#What needs to be done
Here are listed the parts that needs to be implemented. Each one of us should take one or more parts:

  + *Data structures:* Classes to manage data of different type. Classes should implement methods for fetching and storing data to **JSON** file. Some examples are:
    + *Restaurant class:* getter and setters for restaurant name (id maybe?), restaurant picture, list of images (if needed), list of menu(s) [fixed, fixed with options, complete], restaurant owner, location (address), list of orders, list of reviews, timetable, more?
    + *Menu class:* getter and setters for list of courses in the menu, menu type [fixed, fixed with options, complete], description, name, price, picture(s) more? Maybe create a generalized menu class with specialized classes for fixed menus, fixed menus with options, complete menus.
    + *Course class:* getter and setter for type of course [first, main, side, dessert], name, description, price, picture(s). More?
    + *Account class:* getter and setter for account name, id, password, profile picture, account type [user or restaurant owner], list of visited restaurants, list of favourite restaurants, list of eaten courses, list of eaten menus, account balance (money), more?
    + *Review class:* getter and setter for review id, text, user (who submitted the review), review date, rating (stars), reply from the owner (???), more?
    + Something more?
  + *Layouts:* XML files for the layouts of the activities.
  + Activities to implement:
    + Login and Register
    + *Restaurant:* Creation, Management (listing) and view of the single restaurant
    + *Menu:* Creation, Management (listing) and view of the single restaurant
    + *Order:* Management (listing) and view of the single order (details about the order, can take order?, mark as dispatched, etc..)
    + *Review:* view of all the reviews, reply, add new review
  + Mechanisms to implement:
    + *Review system:* Who can review a restaurant? We'd like to allow only those who pay for a menu to be able to review a restaurant. Maybe we should also keep track of how many reviews a user did, so that he's more reliable with respect to other users with less reviews done.
    + *Booking system:* Users should book for a menu, indicating also the time at which the user will show up for consuming the meal, and pay. If the payment is successful, the restaurant owner sees the booking and prepares the order.

#Icons (needed and available)
Icons needed:

  + Gluten-free
  + Vegan
  + Vegetarian
  + Ticket
  + Info
  + Menù
  + Review
  + Spicy/Hot (see oldwildwest.it/Menu/Tex-Mex)
  + **Icon for application (when app is shown in the application drawer)**

Icons already available (through design.google.com/icons):

  + Star
  + Arrow dropdown
  + Search (magnifying glass)
  + Reply
  + Message
  + Sort
  + Swap revert
  + Favourite (for saving restaurants)
  + Add
  + Settings


##Step 2 main point

  * Menu Fixed/MOTD creation activity integration [Giò]
  * Restaurant creation activity integration [Marco]
  * Order management activity integration and layout improvement [Cristiano]
  * Complete menu management activity, drawer management, functional activities [Fede]
