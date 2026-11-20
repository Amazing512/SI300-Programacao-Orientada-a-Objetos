package network;

import java.io.Serializable;
import java.time.LocalDate;

public final class NetworkRequest implements Serializable
   {
   private static final long serialVersionUID = 1L;

   private final String      groupName;
   private final String      groupPassword;
   private final LocalDate   firstDate;
   private final LocalDate   lastDate;

   public NetworkRequest(String groupName, String groupPassword, LocalDate firstDate, LocalDate lastDate)
      {
      super();
      this.groupName     = groupName;
      this.groupPassword = groupPassword;
      this.firstDate     = firstDate;
      this.lastDate      = lastDate;
      }

   public final LocalDate getFirstDate()
      {
      return firstDate;
      }

   public final String getGroupName()
      {
      return groupName;
      }

   public final String getGroupPassword()
      {
      return groupPassword;
      }

   public final LocalDate getLastDate()
      {
      return lastDate;
      }

   }
