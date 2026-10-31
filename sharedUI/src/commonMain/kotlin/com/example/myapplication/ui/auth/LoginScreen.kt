package com.example.myapplication.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.KoakademyLockup

/**
 * Institutional sign-in. Accounts are issued by the school, so the form accepts either the
 * student ID or the institutional email address. Laid out as a printed form: the entries are
 * ruled rather than boxed, and nothing competes with the two things a reader came to do.
 */
@Composable
fun LoginScreen(onSignIn: (String) -> Unit) {
    var account by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var accountError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var resetHintVisible by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current
    val passwordFocus = remember { FocusRequester() }

    fun submit() {
        val accountProblem = validateAccount(account)
        val passwordProblem = validatePassword(password)
        accountError = accountProblem
        passwordError = passwordProblem
        if (accountProblem == null && passwordProblem == null) {
            focusManager.clearFocus()
            onSignIn(account.trim())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .imePadding(),
    ) {
        SignInHead()

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            val viewportHeight = maxHeight
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = viewportHeight)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Welcome back.",
                            style = MaterialTheme.typography.headlineLarge,
                            color = colors.onBackground,
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Sign in with the account issued by your registrar.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurfaceVariant,
                        )

                        Spacer(Modifier.height(40.dp))
                        SlipField(
                            label = "Student ID or email",
                            value = account,
                            onValueChange = {
                                account = it
                                accountError = null
                            },
                            placeholder = "2024-00123 or you@school.edu",
                            error = accountError,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next,
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { passwordFocus.requestFocus() },
                            ),
                        )

                        Spacer(Modifier.height(28.dp))
                        SlipField(
                            label = "Password",
                            value = password,
                            onValueChange = {
                                password = it
                                passwordError = null
                            },
                            placeholder = "At least 8 characters",
                            error = passwordError,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done,
                            ),
                            keyboardActions = KeyboardActions(onDone = { submit() }),
                            visualTransformation = if (passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            focusRequester = passwordFocus,
                            trailing = {
                                Text(
                                    text = if (passwordVisible) "HIDE" else "SHOW",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        letterSpacing = 1.3.sp,
                                    ),
                                    color = colors.secondary,
                                    modifier = Modifier
                                        .clickable { passwordVisible = !passwordVisible }
                                        .padding(start = 12.dp)
                                        .padding(vertical = 12.dp),
                                )
                            },
                        )

                        AnimatedVisibility(visible = resetHintVisible) {
                            Text(
                                text = "Resets are handled by your registrar or IT office.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    lineHeight = 15.5.sp,
                                ),
                                color = colors.onSurfaceVariant,
                                modifier = Modifier.padding(top = 10.dp),
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            Text(
                                text = "Forgot password?",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.5.sp,
                                    textDecoration = TextDecoration.Underline,
                                ),
                                color = colors.secondary,
                                modifier = Modifier
                                    .clickable { resetHintVisible = true }
                                    .padding(vertical = 4.dp),
                            )
                        }

                        Spacer(Modifier.height(30.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .background(colors.primary)
                                .clickable(onClick = ::submit),
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "SIGN IN",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        letterSpacing = 2.sp,
                                    ),
                                    color = colors.onPrimary,
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = "→",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 15.sp,
                                    ),
                                    color = colors.onPrimary,
                                )
                            }
                        }
                    }

                    // Preview accounts close the page, quietly, so they never crowd the fields.
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(Modifier.height(56.dp))
                        HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.7f))
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = "PREVIEW ACCOUNTS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.5.sp,
                                letterSpacing = 1.4.sp,
                            ),
                            color = colors.outline,
                        )
                        SpecimenRow(
                            role = "Student",
                            account = "2024-00123",
                            onPick = {
                                account = "2024-00123"
                                password = previewPassword
                                accountError = null
                                passwordError = null
                            },
                        )
                        SheetHairline()
                        SpecimenRow(
                            role = "Faculty",
                            account = "F-2019-004",
                            onPick = {
                                account = "F-2019-004"
                                password = previewPassword
                                accountError = null
                                passwordError = null
                            },
                        )
                        SheetHairline()
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

/** Running head: brand lockup and nothing else, so the page opens quietly. */
@Composable
private fun SignInHead() {
    val colors = MaterialTheme.colorScheme
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KoakademyLockup(
                markSize = 18.dp,
                wordmarkStyle = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    letterSpacing = 2.4.sp,
                ),
            )
        }
        HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.7f))
    }
}

/**
 * A ruled entry: small-caps label, the value on an underline, and the error where a printed
 * form would put a correction.
 */
@Composable
private fun SlipField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String?,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    focusRequester: FocusRequester? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = MaterialTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val underline = when {
        error != null -> colors.error
        focused -> colors.primary
        else -> colors.outlineVariant
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                letterSpacing = 1.3.sp,
            ),
            color = if (error != null) colors.error else colors.outline,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp)
                    .then(
                        if (focusRequester != null) {
                            Modifier.focusRequester(focusRequester)
                        } else {
                            Modifier
                        },
                    ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.5.sp,
                    letterSpacing = 0.2.sp,
                    color = colors.onSurface,
                ),
                cursorBrush = SolidColor(colors.secondary),
                singleLine = true,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                interactionSource = interactionSource,
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 13.5.sp,
                                ),
                                color = colors.outline,
                                maxLines = 1,
                            )
                        }
                        innerTextField()
                    }
                },
            )
            trailing?.invoke()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (error != null) 1.5.dp else 1.dp)
                .background(underline),
        )
        if (error != null) {
            Spacer(Modifier.height(7.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                ),
                color = colors.error,
            )
        }
    }
}

/** A specimen account, offered as a quiet ruled row rather than a button. */
@Composable
private fun SpecimenRow(role: String, account: String, onPick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = role.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                letterSpacing = 1.3.sp,
            ),
            color = colors.onSurfaceVariant,
            modifier = Modifier.width(76.dp),
        )
        Text(
            text = account,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.5.sp,
            ),
            color = colors.onSurface,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "→",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.secondary,
        )
    }
}

@Composable
private fun SheetHairline() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
    )
}

private const val previewPassword = "preview1234"

private val EmailPattern = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$")
private val StudentIdPattern = Regex("^[A-Za-z0-9][A-Za-z0-9-]{4,}$")

private fun validateAccount(value: String): String? {
    val trimmed = value.trim()
    return when {
        trimmed.isEmpty() -> "Enter your student ID or institutional email"
        trimmed.contains('@') && !EmailPattern.matches(trimmed) ->
            "That email address doesn't look right"

        !trimmed.contains('@') && !StudentIdPattern.matches(trimmed) ->
            "Use your student ID or full email address"

        else -> null
    }
}

private fun validatePassword(value: String): String? = when {
    value.isEmpty() -> "Enter your password"
    value.length < 8 -> "Institutional passwords have at least 8 characters"
    else -> null
}
