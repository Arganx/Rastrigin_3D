package mypackage;

public class Main {

    public static void main(String[] args) {

        int n=10;
        TestData[] test_tab =new TestData[n];
        for(int i=0;i<n;i++)
        {
            test_tab[i]=new TestData();
        }


        Network net= new Network(2,2);

        for(int i=0;i<1;i++) {
            for(int j=0;j<n;j++) {
                net.train(test_tab[j].getTab(), test_tab[j].getResult());
            }
        }

        for(int j=0;j<n;j++) {
            System.out.println("Co mialo wyjsc:"+test_tab[j].getResult());
            System.out.println("Co wyszlo: "+net.guess(test_tab[j].getTab()));

        }
    }
}
