/*
 * Copyright (c) 2022 The Matrix.org Foundation C.I.C.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.matrix.android.sdk.internal.crypto.verification

import org.matrix.android.sdk.api.session.crypto.verification.VerificationTransaction
import org.matrix.android.sdk.internal.crypto.OlmMachine
import org.matrix.android.sdk.internal.crypto.verification.qrcode.QrCodeVerification
import javax.inject.Inject
import javax.inject.Provider
import uniffi.olm.OlmMachine as InnerOlmMachine

internal class VerificationsProvider @Inject constructor(
        private val olmMachine: Provider<OlmMachine>,
        private val verificationRequestFactory: VerificationRequest.Factory,
        private val sasVerificationFactory: SasVerification.Factory,
        private val qrVerificationFactory: QrCodeVerification.Factory) {

    private val innerMachine: InnerOlmMachine
        get() = olmMachine.get().inner()

    fun getVerificationRequests(userId: String): List<VerificationRequest> {
        return innerMachine.getVerificationRequests(userId).map(verificationRequestFactory::create)
    }

    /** Get a verification request for the given user with the given flow ID */
    fun getVerificationRequest(userId: String, flowId: String): VerificationRequest? {
        return innerMachine.getVerificationRequest(userId, flowId)?.let { innerVerificationRequest ->
            verificationRequestFactory.create(innerVerificationRequest)
        }
    }

    /** Get an active verification for the given user and given flow ID.
     *
     * @return Either a [SasVerification] verification or a [QrCodeVerification]
     * verification.
     */
    fun getVerification(userId: String, flowId: String): VerificationTransaction? {
        return when (val verification = innerMachine.getVerification(userId, flowId)) {
            is uniffi.olm.Verification.QrCodeV1 -> {
                val request = getVerificationRequest(userId, flowId) ?: return null
                qrVerificationFactory.create(request, verification.qrcode)
            }
            is uniffi.olm.Verification.SasV1    -> {
                sasVerificationFactory.create(verification.sas)
            }
            null                                -> {
                // This branch exists because scanning a QR code is tied to the QrCodeVerification,
                // i.e. instead of branching into a scanned QR code verification from the verification request,
                // like it's done for SAS verifications, the public API expects us to create an empty dummy
                // QrCodeVerification object that gets populated once a QR code is scanned.
                val request = getVerificationRequest(userId, flowId) ?: return null
                if (request.canScanQrCodes()) {
                    qrVerificationFactory.create(request, null)
                } else {
                    null
                }
            }
        }
    }
}
