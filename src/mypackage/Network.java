package mypackage;

import java.util.List;
import java.util.Scanner;

/**
 * Created by qwerty on 25-Nov-17.
 */
public class Network {
    /*
        TODO
        algorytm zgadywania


     */
    private int number_of_layers;
    private int number_of_entrence_data;

    private Neuron[][] layers;

    private int[] neurons_in_layer;     //Indeks to numer warstwy

    public Network(int number_of_layers,int number_of_entrance_data) {
        this.number_of_layers = number_of_layers;
        this.number_of_entrence_data=number_of_entrance_data;
        neurons_in_layer=new int[number_of_layers];

        layers=new Neuron[number_of_layers][];
        Scanner in = new Scanner(System.in);
        for(int i=0;i<number_of_layers;i++)
        {
            System.out.println("Podaj ile neuronow ma liczyc warstwa " + (i));
            if(i==number_of_layers-1)
            {
                System.out.println("OSTATNIA");
            }
            int tmp = in.nextInt();
            neurons_in_layer[i]=tmp;
            layers[i]=new Neuron[tmp];
        }

        for(int i=0;i<number_of_layers;i++)     //budowa siatki
        {
            for(int j=0;j<neurons_in_layer[i];j++)
            {
                if(i!=0) {
                    layers[i][j] = new Neuron(neurons_in_layer[i - 1]);     //kazdy neuron w jednej warstwie ma tyle samo wejsc, tyle ile wyjsc z poprzedniej warstwy
                }
                else
                {
                    layers[i][j]=new Neuron(number_of_entrance_data);       //pierwsza warstwa dostaje tyle wejsc ile wynosi ilosc danych wejsciowych
                }
            }
        }
    }


    public double guess(double[] input)     //zwraca wynik calej sieci
    {
        int max_number_of_neurons_in_layer=0;
        for(int i=0;i<neurons_in_layer.length;i++)
        {
            if(neurons_in_layer[i]>max_number_of_neurons_in_layer)
            {
                max_number_of_neurons_in_layer=neurons_in_layer[i];
            }
        }
        double[] help_tab = new double[max_number_of_neurons_in_layer];

        //Najpierw warstwa 1 musi zgadywac
        for(int i=0;i<neurons_in_layer[0];i++)  //Idac po neuronach w warstwie
        {
            help_tab[i]=layers[0][i].guess(input);      //trzeba zapisac wyniki gdyz stanowia wejscie do kolejnej warstwy
        }

        //Potem kolejne na podstawie poprzedniej
        double[] temp_help_tab = new double[max_number_of_neurons_in_layer];        //Potrzebna zeby neurony nie zmienialy danych wejsciowych w tej samej warstwie
        for(int i=1;i<number_of_layers;i++)
        {
            for(int j=0;j<neurons_in_layer[i];j++)
            {
                temp_help_tab[j]=layers[i][j].guess(help_tab);
            }
            help_tab=temp_help_tab;
        }
        return help_tab[0];     //Zakladam ze ostatnia warstwa to pojedynczy neuron i zwracam jego wynik
    }


    public void train(double[] input,double result)
    {
        double guess =this.guess(input);
        double error = result-guess;

        layers[number_of_layers - 1][0].setError(error);

        //ustawienie errorow poprzednich warstw
        for(int i=number_of_layers-2;i>-1;i--)   //warstwy
        {

            for(int j=0;j<neurons_in_layer[i];j++)      //pojedyncze neurony w wyliczanej warstwie
            {
                for(int k=0;k<neurons_in_layer[i+1];k++)    //liczba neuronow w poprzedniej warstwie
                {
                    //System.out.println(layers[i][j].getError()+"+"+layers[i + 1][k].getError()+"*"+layers[i + 1][k].get_weight(j));
                    layers[i][j].setError(layers[i][j].getError() + layers[i + 1][k].getError() * layers[i + 1][k].get_weight(j));        //TODO SPRAWDZIC error rosnie o error poprzedniej warstwy razy waga polaczenie pomiedzy neuronami
                }
            }
        }

        //wyliczenie nowych wag
        //tablica pomocnicza

        double[][] help_tab = new double[number_of_layers][]; //wyniki poszczegolnych neuronow
        for(int i=0;i<number_of_layers;i++) {
            help_tab[i] = new double[neurons_in_layer[i]];
        }

        //Najpierw warstwa 1 musi zgadywac
        for(int i=0;i<neurons_in_layer[0];i++)  //Idac po neuronach w warstwie 1
        {
            help_tab[0][i] = layers[0][i].guess(input);      //trzeba zapisac wyniki gdyz stanowia wejscie do kolejnej warstwy
        }

        //Potem kolejne
        for(int i=1;i<number_of_layers;i++)
        {
            for(int j=0;j<neurons_in_layer[i];j++)
            {
                help_tab[i][j] = layers[i][j].guess(help_tab[i - 1]);
            }
        }
        Double[] tmp;
        //Liczenie nowych wag dla kazdego neuronu
        for(int i=0;i<number_of_layers;i++) {
            for (int j = 0; j < neurons_in_layer[i]; j++) {
                tmp = layers[i][j].getWeights();
                for (int k = 0; k < tmp.length; k++) {
                    if (i != 0) {
                        tmp[k] += layers[i][j].getLr() * layers[i][j].getError() * layers[i][j].activation_derrivative(layers[i][j].sum(help_tab[i - 1])) * help_tab[i - 1][k];
                    } else {
                        tmp[k] += layers[i][j].getLr() * layers[i][j].getError() * layers[i][j].activation_derrivative(layers[i][j].sum(input)) * input[k];
                    }
                }
                layers[i][j].setWeights(tmp);
            }
        }
        //koniec wag
    }
}
