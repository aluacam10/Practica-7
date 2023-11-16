//Rodriguez del Angel Erick Manuel-Practica 7 Final//
//3°B ISC//

import Cajero.Billetes;
import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class CajeroAutomaticoMain {
     private static final String BILLETES = "billetes";
        private static final String registros = "registros";

        private static final Random random = new Random();

        public static void main(String[] args) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Bienvenido al Cajero Automático UACAM");

            System.out.print("Por favor Ingrese su nombre: ");
            String nombre = scanner.next();

            System.out.print("Ingrese su NIP para Acceder al Cajero");
            int nip = scanner.nextInt();

            if (nombre.equalsIgnoreCase("admin") && nip == 3243) {
                modoAdministrador();
            } else {
                modoCajero(nombre);
            }
        }

        private static void modoCajero(String usuario) {
            int saldo = obtenerSaldoAleatorio();
            cargarBilletes();

            System.out.println("Bienvenido, " + usuario + ". Su saldo actual es: $" + saldo);

            while (true) {
                System.out.println("Acciones disponibles:");
                System.out.println("1. Consultar saldo");
                System.out.println("2. Retirar efectivo");
                System.out.println("3. Salir del cajero");
                System.out.print("Seleccione una opción: ");

                Scanner scanner = new Scanner(System.in);
                int opcion = scanner.nextInt();

                switch (opcion) {
                    case 1 -> consultarSaldo(usuario, saldo);
                    case 2 -> retirarEfectivo(usuario, saldo);
                    case 3 -> {
                        guardarLogs("Salir", usuario, saldo, "SI");
                        System.out.println("Gracias por utilizar el Cajero Automático. ¡Hasta luego!");
                        System.exit(0);
                    }
                    default -> System.out.println("Opción no válida. Intente de nuevo.");
                }
            }
        }

        private static void modoAdministrador() {
            while (true) {
                System.out.println("Acciones disponibles para el administrador:");
                System.out.println("1. Mostrar acciones en el log");
                System.out.println("2. Mostrar cantidad de billetes restantes");
                System.out.println("3. Salir del modo administrador");
                System.out.print("Seleccione una opción: ");

                Scanner scanner = new Scanner(System.in);
                int opcion = scanner.nextInt();

                switch (opcion) {
                    case 1 -> mostrarLog();
                    case 2 -> mostrarCantidadBilletes();
                    case 3 -> {
                        System.out.println("Saliendo del modo administrador.");
                        System.exit(0);
                    }
                    default -> System.out.println("Opción no válida. Intente de nuevo.");
                }
            }
        }

        private static void mostrarCantidadBilletes() {

        }

        private static int obtenerSaldoAleatorio() {
            return random.nextInt(49001) + 1000;
        }

        private static void cargarBilletes() {
            File archivo = new File(BILLETES);

            if (!archivo.exists()) {
                try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(BILLETES))) {
                    Billetes billetes = new Billetes(100, 100, 20, 10);
                    outputStream.writeObject(billetes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private static void consultarSaldo(String usuario, int saldo) {
            System.out.println("Su saldo actual es: $" + saldo);
            guardarLogs("Consultar", usuario, saldo, "SI");
        }

        private static void retirarEfectivo(String usuario, int saldo) {
            System.out.print("Ingrese la cantidad que desea retirar: ");
            Scanner scanner = new Scanner(System.in);
            int cantidadRetiro = scanner.nextInt();

            if (cantidadRetiro <= saldo) {
                Billetes billetesDisponibles = obtenerBilletesDisponibles();

                assert billetesDisponibles != null;
                if (verificarBilletesSuficientes(cantidadRetiro, billetesDisponibles)) {
                    // Realizar el retiro
                    descontarBilletes(cantidadRetiro, billetesDisponibles);
                    guardarBilletes(billetesDisponibles);
                    saldo -= cantidadRetiro;
                    System.out.println("Retiro exitoso. Nuevo saldo: $" + saldo);
                    guardarLogs("Retirar", usuario, cantidadRetiro, "SI");
                } else {
                    System.out.println("No hay suficientes billetes para realizar el retiro. Intente con otra cantidad.");
                    guardarLogs("Retirar", usuario, cantidadRetiro, "NO");
                }
            } else {
                System.out.println("Saldo insuficiente. por favor Intente con una cantidad menor.");
                guardarLogs("Retirar", usuario, cantidadRetiro, "NO");
            }
        }
        private static Billetes obtenerBilletesDisponibles() {
            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(BILLETES))) {
                return (Billetes) inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        private static boolean verificarBilletesSuficientes(int cantidadRetiro, Billetes billetesDisponibles) {
            int totalDisponible = billetesDisponibles.getTotal();
            return cantidadRetiro <= totalDisponible;
        }

        private static void descontarBilletes(int cantidadRetiro, Billetes billetesDisponibles) {
            int[] denominaciones = {100, 200, 500, 1000};

            for (int i = denominaciones.length - 1; i >= 0; i--) {
                int billetes = cantidadRetiro / denominaciones[i];
                if (billetes > 0 && billetesDisponibles.getCantidad(i) >= billetes) {
                    cantidadRetiro -= billetes * denominaciones[i];
                    billetesDisponibles.descontar(i, billetes);
                }
            }
        }
        private static void guardarBilletes(Billetes billetes) {
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(BILLETES))) {
                outputStream.writeObject(billetes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private static void guardarLogs(String accion, String usuario, int cantidad, String seRealizo) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(registros, true))) {
                writer.println(accion + "," + usuario + "," + cantidad + "," + seRealizo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private static void mostrarLog() {
            try (BufferedReader reader = new BufferedReader(new FileReader(registros))) {
                String linea;
                boolean line = false;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }