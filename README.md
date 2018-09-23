# How to proceed

Malnati suggests to split the work on the repository by functionalities rather than activities. This is because it is unlikely to have activities that are totally independent from others and, especially, from data structures. Also it's better to split the design (layouts) from the programming.

The approach each one of us should take once the tasks are assigned is the following: you create a branch representing the functionality you are implementing. You keep working on YOUR branch and once it is completed you inform the *"team leader"*. The *team leader*, other than working on his branch, is in charge also of merging your branch with the master. You **MUST NOT** merge your branch by yourself.

# What needs to be done

Here are listed the parts that needs to be implemented. Each one of us should take one or more parts:

  + Mechanisms to implement:
    + *Review system:* Who can review a restaurant? We'd like to allow only those who pay for a menu to be able to review a restaurant. Maybe we should also keep track of how many reviews a user did, so that he's more reliable with respect to other users with less reviews done.
    + *Booking system:* Users should book for a menu, indicating also the time at which the user will show up for consuming the meal, and pay. If the payment is successful, the restaurant owner sees the booking and prepares the order.

# TODO

# Database Design
The images are stored as they are (JPG/PNG/GIF) inside Firebase (we have a small area where we can store them). What we save inside the DB is just the name of the image (which must be unique, so it will likely be an ID).

The entries with a *(??)* may not be needed, maybe we can remove them. Let's aim for a cleaner data structures, so it will be easier to manage.
Here is a proposed design for Firebase Database:

    ROOT
      |-Course
      |    |-ID
      |      |-Menu ID
      |      |-Name
      |      |-Description (??)
      |      |-Price (??)
      |      |-Image Name (??)
      |      |-Gluten Free
      |      |-Vegan
      |      |-Spicy
      |      |-Tag (List of strings)
      |-Menu
      |    |-ID
      |      |-Restaurant ID
      |      |-Name
      |      |-Description
      |      |-Price
      |      |-Image Name
      |      |-Course ID (List of Course IDs)
      |      |-Ticket (??)
      |      |-Beverage
      |      |-Service Fee
      |-Order
      |    |-ID
      |      |-Restaurant ID
      |      |-Menu ID
      |      |-User ID
      |      |-Date
      |      |-Notes
      |      |-Name
      |-Restaurant
      |    |-ID
      |      |-User ID
      |      |-Name
      |      |-Description
      |      |-Address
      |      |-City
      |      |-ZIPCode
      |      |-State
      |      |-Website
      |      |-Telephone
      |      |-Image Name
      |      |-Rating
      |      |-xcoord
      |      |-ycoord
      |      |-Menu ID (List of Menu IDs)
      |      |-Order ID (List of Order IDs)
      |      |-Ticket
      |         |-ID
      |            |-Name
      |      |-Review ID (List of Review IDs)
      |      |-Timetable
      |          |-Monday
      |          |    |-Lunch
      |          |    |    |-Star Time
      |          |    |    |-End Time
      |          |    |-Dinner
      |          |        |-Star Time
      |          |        |-End Time
      |          |-Tuesday
      |          |    |-Lunch
      |          |    |    |-Star Time
      |          |    |    |-End Time
      |          |    |-Dinner
      |          |        |-Star Time
      |          |        |-End Time
      |          |-Wednesday
      |          |    |-Lunch
      |          |    |    |-Star Time
      |          |    |    |-End Time
      |          |    |-Dinner
      |          |        |-Star Time
      |          |        |-End Time
      |          |-Thursday
      |          |    |-Lunch
      |          |    |    |-Star Time
      |          |    |    |-End Time
      |          |    |-Dinner
      |          |        |-Star Time
      |          |        |-End Time
      |          |-Friday
      |          |    |-Lunch
      |          |    |    |-Star Time
      |          |    |    |-End Time
      |          |    |-Dinner
      |          |        |-Star Time
      |          |        |-End Time
      |          |-Saturday
      |          |    |-Lunch
      |          |    |    |-Star Time
      |          |    |    |-End Time
      |          |    |-Dinner
      |          |        |-Star Time
      |          |        |-End Time
      |          |-Sunday
      |              |-Lunch
      |              |    |-Star Time
      |              |    |-End Time
      |              |-Dinner
      |                  |-Star Time
      |                  |-End Time
      |-User
      |    |-Restaurant Owner
      |    |    |-ID
      |    |      |-Name
      |    |      |-Surname
      |    |      |-Username
      |    |      |-Password
      |    |      |-Email
      |    |      |-Address
      |    |      |-Image Name
      |    |      |-Review (List of Review IDs)
      |    |      |-Restaurant (List of Restaurant IDs) (??)
      |    |-Customer
      |        |-ID
      |          |-Name
      |          |-Surname
      |          |-Username
      |          |-Password
      |          |-Email
      |          |-Address
      |          |-Image Name
      |          |-Review (List of Review IDs) 
      |          |-Favourite (List of Restaurant IDs)     
      |-Review
          |-ID
            |-Restaurant ID
            |-User ID
            |-Title
            |-Text
            |-Date
            |-Rating
            |-Reply
                |-ID
                  |-User ID
                  |-Title
                  |-Text


