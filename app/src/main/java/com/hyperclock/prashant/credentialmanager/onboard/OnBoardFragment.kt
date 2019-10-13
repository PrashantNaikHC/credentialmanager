package com.hyperclock.prashant.credentialmanager.onboard



import android.app.Application
import android.app.FragmentManager
import android.app.KeyguardManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hyperclock.prashant.credentialmanager.databinding.FragmentOnBoardBinding
import android.content.Context
import android.content.SharedPreferences
import android.hardware.fingerprint.FingerprintManager
import android.preference.PreferenceManager
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.core.content.ContextCompat.getSystemService
import com.hyperclock.prashant.credentialmanager.R
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import java.util.concurrent.Executor
import javax.crypto.*

class OnBoardFragment : Fragment() , FingerprintAuthenticationDialogFragment.Callback {

    companion object {
        private val ANDROID_KEY_STORE = "AndroidKeyStore"
        private val DIALOG_FRAGMENT_TAG = "myFragment"
        private val KEY_NAME_NOT_INVALIDATED = "key_not_invalidated"
        private val SECRET_MESSAGE = "Very secret message"
        private val TAG = this.javaClass.simpleName
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with a fingerprint.
     *
     * @param keyName the name of the key to be created
     * @param invalidatedByBiometricEnrollment if `false` is passed, the created key will not be
     * invalidated even if a new fingerprint is enrolled. The default value is `true` - the key will
     * be invalidated if a new fingerprint is enrolled.
     */
    override fun createKey(keyName: String, invalidatedByBiometricEnrollment: Boolean) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of enrolled
        // fingerprints has changed.
        try {
            keyStore.load(null)

            val keyProperties = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            val builder = KeyGenParameterSpec.Builder(keyName, keyProperties)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment)

            keyGenerator.run {
                init(builder.build())
                generateKey()
            }
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is InvalidAlgorithmParameterException,
                is CertificateException,
                is IOException -> throw RuntimeException(e)
                else -> throw e
            }
        }
    }


    private lateinit var keyStore: KeyStore
    private lateinit var keyGenerator: KeyGenerator
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var binding: FragmentOnBoardBinding
    private lateinit var viewmodel: OnboardViewModel
    private lateinit var application: Application
    private val executor = Executor { }

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_on_board, container, false)

        viewmodel = ViewModelProviders.of(this).get(OnboardViewModel::class.java)
        binding.onboardViewmodelVariable = viewmodel
        binding.setLifecycleOwner(this)

        application = requireNotNull(this.activity).application
        binding.entryPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(arg0: Editable) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun onTextChanged(strings: CharSequence, a: Int, b: Int, c: Int) {
                if (viewmodel.checkPassword(binding.entryPassword.text.toString())) {
                    removePhoneKeypad()
                    Snackbar.make(
                        activity!!.findViewById(android.R.id.content),
                        getString(R.string.login_message),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(OnBoardFragmentDirections.actionOnBoardDestinationToHomeDestination())
                }
            }
        })

        viewmodel.randomString.observe(this, Observer { pass ->
            //binding.hintText.text = pass.toString()
            val layout = binding.linearLayout
            for (i in 0..pass.size - 1) {

                val progressBar =
                    ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal)
                progressBar.apply {
                    //scaleX = (pass.indexOf(i)*0.9).toFloat()
                    max = 10
                    isIndeterminate = false
                    progress = pass[i]
                    Log.d("OnBoardFragment", "value is $i with progress $progress")
                }
                layout.addView(progressBar)
            }
        })

        /**
         * Setting up the android keystore and keygenerator
         */
        setupKeyStoreAndKeyGenerator()


        val (defaultCipher: Cipher, cipherNotInvalidated: Cipher) = setupCiphers()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context)
        setLoginButtons(cipherNotInvalidated, defaultCipher)

        val biometricManager = BiometricManager.from(application)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                showToast("Fingerprint exists")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                showToast("No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                showToast("Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                showToast("The user hasn't associated any biometric credentials with their account.")
        }

        (activity as AppCompatActivity).supportActionBar?.title =
            getString(com.hyperclock.prashant.credentialmanager.R.string.login_string)

        return binding.root
    }

    private fun setLoginButtons(cipherNotInvalidated: Cipher, defaultCipher: Cipher) {

        val keyguardManager = activity?.getSystemService(KeyguardManager::class.java)
        if (!keyguardManager!!.isKeyguardSecure) {
            // Show a message that the user hasn't set up a fingerprint or lock screen.
            showToast(getString(R.string.setup_lock_screen))
            binding.fingerprintFab.isEnabled = false
            return
        }

        val fingerprintManager = activity?.getSystemService(FingerprintManager::class.java)
        if (!fingerprintManager!!.hasEnrolledFingerprints()) {
            binding.fingerprintFab.isEnabled = false
            // This happens when no fingerprints are registered.
            showToast(getString(R.string.register_fingerprint))
            return
        }

        createKey(DEFAULT_KEY_NAME)
        createKey(KEY_NAME_NOT_INVALIDATED, false)
        binding.fingerprintFab.run {
            isEnabled = true
            setOnClickListener(PurchaseButtonClickListener(defaultCipher, DEFAULT_KEY_NAME))
        }
    }


    /**
     * Initialize the [Cipher] instance with the created key in the [createKey] method.
     *
     * @param keyName the key name to init the cipher
     * @return `true` if initialization succeeded, `false` if the lock screen has been disabled or
     * reset after key generation, or if a fingerprint was enrolled after key generation.
     */
    private fun initCipher(cipher: Cipher, keyName: String): Boolean {
        try {
            keyStore.load(null)
            cipher.init(Cipher.ENCRYPT_MODE, keyStore.getKey(keyName, null) as SecretKey)
            return true
        } catch (e: Exception) {
            when (e) {
                is KeyPermanentlyInvalidatedException -> return false
                is KeyStoreException,
                is CertificateException,
                is UnrecoverableKeyException,
                is IOException,
                is NoSuchAlgorithmException,
                is InvalidKeyException -> throw RuntimeException("Failed to init Cipher", e)
                else -> throw e
            }
        }
    }

    private inner class PurchaseButtonClickListener internal constructor(
        internal var cipher: Cipher,
        internal var keyName: String
    ) : View.OnClickListener {

        override fun onClick(view: View) {

            val fragment = FingerprintAuthenticationDialogFragment()
            fragment.setCryptoObject(FingerprintManager.CryptoObject(cipher))
            fragment.setCallback(this@OnBoardFragment)

            // Set up the crypto object for later, which will be authenticated by fingerprint usage.
            if (initCipher(cipher, keyName)) {

                // Show the fingerprint dialog. The user has the option to use the fingerprint with
                // crypto, or can fall back to using a server-side verified password.
                val useFingerprintPreference = sharedPreferences
                    .getBoolean(getString(R.string.use_fingerprint_to_authenticate_key), true)
                if (useFingerprintPreference) {
                    fragment.setStage(Stage.FINGERPRINT)
                } else {
                    fragment.setStage(Stage.PASSWORD)
                }
            } else {
                // This happens if the lock screen has been disabled or or a fingerprint was
                // enrolled. Thus, show the dialog to authenticate with their password first and ask
                // the user if they want to authenticate with a fingerprint in the future.
                fragment.setStage(Stage.NEW_FINGERPRINT_ENROLLED)
            }
            fragment.show(activity?.fragmentManager, DIALOG_FRAGMENT_TAG)
        }
    }

    /**
     * Generate a pair of ciphers one for default value and other for not Invalidated
     */
    private fun setupCiphers(): Pair<Cipher, Cipher> {
        val defaultCipher: Cipher
        val cipherNotInvalidated: Cipher
        try {
            val cipherString =
                "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"
            defaultCipher = Cipher.getInstance(cipherString)
            cipherNotInvalidated = Cipher.getInstance(cipherString)
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is NoSuchPaddingException ->
                    throw RuntimeException("Failed to get an instance of Cipher", e)
                else -> throw e
            }
        }
        return Pair(defaultCipher, cipherNotInvalidated)
    }

    private fun setupKeyStoreAndKeyGenerator() {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to get an instance of KeyStore", e)
        }

        try {
            keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is NoSuchProviderException ->
                    throw RuntimeException("Failed to get an instance of KeyGenerator", e)
                else -> throw e
            }
        }
    }

    override fun onPurchased(withFingerprint: Boolean, crypto: FingerprintManager.CryptoObject?) {
        if (withFingerprint) {
            // If the user authenticated with fingerprint, verify using cryptography and then show
            // the confirmation message.
            if (crypto != null) {
                tryEncrypt(crypto.cipher)
            }
        } else {
            // Authentication happened with backup password. Just show the confirmation message.
            showConfirmation()
        }
    }

    /**
     * Tries to encrypt some data with the generated key from [createKey]. This only works if the
     * user just authenticated via fingerprint.
     */
    private fun tryEncrypt(cipher: Cipher) {
        try {
            showConfirmation(cipher.doFinal(SECRET_MESSAGE.toByteArray()))
        } catch (e: Exception) {
            when (e) {
                is BadPaddingException,
                is IllegalBlockSizeException -> {
                    Toast.makeText(
                        activity, "Failed to encrypt the data with the generated key. "
                                + "Retry the purchase", Toast.LENGTH_LONG
                    ).show()
                    Log.e(TAG, "Failed to encrypt the data with the generated key. ${e.message}")
                }
                else -> throw e
            }
        }
    }

    fun removePhoneKeypad() {
        val inputManager = view!!
            .getContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val binder = view!!.getWindowToken()
        inputManager.hideSoftInputFromWindow(
            binder,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    // Show confirmation message. Also show crypto information if fingerprint was used.
    private fun showConfirmation(encrypted: ByteArray? = null) {
        findNavController().navigate(OnBoardFragmentDirections.actionOnBoardDestinationToHomeDestination())
        if (encrypted != null) {
            //var value = Base64.encodeToString(encrypted, 0 /* flags */)
        }
    }
}



