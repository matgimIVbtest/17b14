package rs.edu.matgim.zadatak;

import java.sql.*;
import java.util.List;

public class DB {

    String connectionString = "jdbc:sqlite:src\\main\\java\\KompanijaZaPrevoz.db";

    public void printFirma() {
        try (Connection conn = DriverManager.getConnection(connectionString); Statement s = conn.createStatement()) {

            ResultSet rs = s.executeQuery("SELECT * FROM Firma");
            while (rs.next()) {
                int IdFil = rs.getInt("IdFir");
                String Naziv = rs.getString("Naziv");
                String Adresa = rs.getString("Adresa");
                String Tel1 = rs.getString("Tel1");
                String Tel2 = rs.getString("Tel2");

                System.out.println(String.format("%d\t%s\t%s\t%s\t%s", IdFil, Naziv, Adresa, Tel1, Tel2));
            }

        } catch (SQLException ex) {
            System.out.println("Greska prilikom povezivanja na bazu");
            System.out.println(ex);
        }
    }
    public void printUkupnaTezina() {
        try (Connection conn = DriverManager.getConnection(connectionString); Statement s = conn.createStatement()) {

            ResultSet rs = s.executeQuery("SELECT P.IDPut, P.MestoOd, P.MestoDo, P.Duzina, sum(Tezina) FROM Putovanje P, Posiljka O, SePrevozi S WHERE S.IDPut=P.IDPut AND S.IDPos==O.IDPos GROUP BY P.IDPut");
            while (rs.next()) {
                int idput=rs.getInt(1);
                String mestood=rs.getString(2);
                String mestodo=rs.getString(3);
                int duzina=rs.getInt(4);
                int tezina=rs.getInt(5);

                System.out.println(String.format("%d\t%s\t%s\t%d\t%d", idput, mestood, mestodo, duzina, tezina));
            }

        } catch (SQLException ex) {
            System.out.println("Greska prilikom povezivanja na bazu");
            System.out.println(ex);
        }
    }
    public boolean zadatak(int IdKam) {
        try (Connection conn = DriverManager.getConnection(connectionString); Statement s = conn.createStatement()) {

            conn.setAutoCommit(false);
            PreparedStatement ss = conn.prepareStatement("UPDATE Kamion Set BrPopravljanja = BrPopravljanja + ? WHERE IDKam = ?") ;
            ss.setInt(1,1);
            ss.setInt(2, IdKam);
            ss.execute();
            int[] radnici = new int[100];
            int[] brdana = new int[100];
            ResultSet rs = s.executeQuery("SELECT IDZap, Dana FROM popravlja WHERE IDKam="+IdKam);
            int i=0;
            while (rs.next()) {
                int k =rs.getInt("IDZap");
                int l =rs.getInt("Dana");
                radnici[i]=k;
                brdana[i]=l;
                i++;
            }
            int brojradnika=i;
            int j=0;
            PreparedStatement del = conn.prepareStatement("DELETE FROM Popravlja WHERE IDKam=?");
            del.setInt(1, IdKam);
            del.execute();
            ResultSet nadji=s.executeQuery("SELECT DISTINCT IDKam FROM Popravlja");
            while(nadji.next() && j<brojradnika)
            {
                int kamionce=nadji.getInt(1);
                PreparedStatement sss = conn.prepareStatement("INSERT INTO Popravlja VALUES (?,?,?)");
                sss.setInt(1, brdana[j]);
                sss.setInt(2, radnici[j]);
                sss.setInt(3, kamionce);
                sss.execute();
                j++;
            }
            conn.commit();
            conn.setAutoCommit(true);
            System.out.println("Ostali radnici:\n");
            while(j<brojradnika)
            {
                System.out.println(radnici[j]+"\t");
                j++;
            }
            System.out.println("Uspešna realizacija");
            return true;

        } catch (SQLException ex) {
            System.out.println("Dogodila se greška");
            System.out.println(ex);
            return false;
        }
    }
    

}
