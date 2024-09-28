package cl.negocio.prueba

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.content.Intent
import android.widget.TextView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)

        // Inicializa FirebaseAuth
        auth = FirebaseAuth.getInstance()

        val btn: Button = findViewById(R.id.loginButton)
        val correo: TextView = findViewById(R.id.correo)
        val contrasena: TextView = findViewById(R.id.contrasena)

        // Evento de clic para el botón de inicio de sesión
        btn.setOnClickListener {
            val email = correo.text.toString()
            val password = contrasena.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signIn(email, password)
            } else {
                Toast.makeText(this, "Por favor, ingrese correo y contraseña", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Función para manejar el inicio de sesión
    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // El inicio de sesión fue exitoso, accede al usuario actual
                    val user = auth.currentUser
                    Toast.makeText(this, "Inicio de sesión exitoso: ${user?.uid}", Toast.LENGTH_SHORT).show()

                    // Redirigir a la nueva actividad
                    val intent = Intent(this, MenuActivity::class.java) // Corregido aquí
                    startActivity(intent)
                } else {
                    // Error de autenticación
                    Toast.makeText(this, "Error de autenticación.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // Maneja los errores
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

