##TODO

  + Menu data function to recover menu icons & tags
  
##Fix list

  + ~~Check if activity selected from drawer is the same of the one currently displayed. If so, don't load it again.~~
  + **Register page**:

      + (??? Maybe not a good idea)Put switch instead checkbox for restaurant owner.
      + ~~Adjust layout of second page by vertical aligning all the fields.~~
      + ~~Add checks for empty fields.~~
      + ~~Change text views as single-line texts~~
      + ~~Change email field as email address field type.~~
      + ~~Change password field as password field type.~~
      + ~~Do we want 2 fragments? One not enough?~~

  + **Login page**:

      + Close the keyboard after you login. Currently, it remains open.

  + **Edit Restaurant Information**:

     + Freeze due to low memory... Can this be solved??

  + **List of all restaurant in restauranta owner page**:

    + Gear icon -> Delete doesn't work.

  + **Create restaurant**:

    + Add range limit for timetables (extend timepicker properly).

  + **Create Menu**:

    + Crash on creation of daily menu: Add name of menu, add description, do NOT add image, press next, crash due to NullPointerException.
    + Create menu is not usable on low-end devices due to high-memory consumption
    + ~~Translation of "Complete Menu" should be "Menu Completo" and not "Completa il Menu"~~
    + Crash on Create_simple_menu1.java:293 when adding image.