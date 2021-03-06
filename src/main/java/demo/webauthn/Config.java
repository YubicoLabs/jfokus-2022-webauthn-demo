// Copyright 2022 Yubico AB
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package demo.webauthn;

import com.yubico.webauthn.data.RelyingPartyIdentity;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Config {

  public static final int port = 8443;

  public static final Set<String> origins =
      Collections.unmodifiableSet(
          new HashSet<>(Arrays.asList("https://localhost:8443", "https://localhost:8080")));

  public static final RelyingPartyIdentity rpIdentity =
      RelyingPartyIdentity.builder().id("localhost").name("WebAuthn demo").build();
}
