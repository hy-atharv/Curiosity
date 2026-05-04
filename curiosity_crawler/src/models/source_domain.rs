use serde::{Deserialize, Serialize};

#[derive (Serialize, Deserialize, Debug)]
pub struct SourceDomain {
    pub name: String,
    pub trust_score: f32,
    pub inbound_domains: Vec<String>,
    pub outbound_domains: Vec<String>,
}


impl SourceDomain {

    pub fn new(domain_name: &str) -> SourceDomain {
        Self {
            name: domain_name.to_string(),
            trust_score: Self::get_initial_trust_score(domain_name.trim()),
            inbound_domains: vec![],
            outbound_domains: vec![],
        }
    }

    pub fn get_initial_trust_score(name: &str) -> f32 {
        // CHECK FOR INTER-GOV OR GOV DOMAINS
        if Self::is_trusted_gov_domain(name) {
            0.8
        }
        // CHECK FOR TRUSTED ACADEMIC DOMAINS
        else if Self::is_trusted_academic_domain(name) {
            0.8
        }
        // CHECK FOR TRUSTED RESEARCH DOMAINS
        else if Self::is_trusted_research_domain(name) {
            0.8
        }
        // CHECK FOR TRUSTED HEALTHCARE DOMAINS
        else if Self::is_trusted_healthcare_domain(name) {
            0.8
        }
        // CHECK FOR TRUSTED FINANCIAL DOMAINS
        else if Self::is_trusted_financial_domain(name) {
            0.8
        }
        // CHECK FOR TRUSTED LAW DOMAINS
        else if Self::is_trusted_law_domain(name) {
            0.8
        }
        // INITIAL TRUST
        else {
            0.6
        }
    }

    pub fn increment_trust_score(
        &mut self,
        inbound_domain_trust_score: f32
    ) {
        self.trust_score += inbound_domain_trust_score * 0.5;
    }

    pub fn add_inbound_domain(&mut self, inbound_domain: &str) {
        self.inbound_domains.push(inbound_domain.to_string());
    }

    pub fn add_outbound_domain(&mut self, outbound_domain: &str) {
        self.outbound_domains.push(outbound_domain.to_string());
    }

    pub fn is_trusted_gov_domain(name: &str) -> bool {
        let gov_cc = vec![
            "au",
            "bd",
            "br",
            "ca",
            "cn",
            "cy",
            "eg",
            "gh",
            "hk",
            "in",
            "il",
            "jm",
            "ke",
            "lk",
            "mw",
            "my",
            "ng",
            "nz",
            "ph",
            "pk",
            "qa",
            "sa",
            "sg",
            "th",
            "tz",
            "uk",
            "us",
            "za",
            "zw"
        ];

        if name.ends_with(".int")
            || name.ends_with(".gov") // US
            || name.ends_with(".gouv.fr") // FRANCE
            || name.ends_with(".go.jp") // JAPAN
            || name.ends_with(".bund.de") // GERMANY
        {
            return true;
        }

        gov_cc.iter().any(|cc| {
            name.ends_with(&format!(".gov.{cc}")) // Countries with gov.cc
        })
    }

    pub fn is_trusted_academic_domain(name: &str) -> bool {
        let academic_cc = vec![
            "au",
            "bd",
            "cn",
            "hk",
            "id",
            "in",
            "ir",
            "jp",
            "ke",
            "kr",
            "lk",
            "my",
            "ng",
            "np",
            "nz",
            "ph",
            "pk",
            "sg",
            "th",
            "tz",
            "uk"
        ];

        if name.ends_with(".edu") // US
            || name.ends_with(".ac")
        {
            return true;
        }

        academic_cc.iter().any(|cc| {
            name.ends_with(&format!(".edu.{cc}")) || name.ends_with(&format!(".ac.{cc}")) // Countries with edu.cc or ac.cc
        })
    }

    pub fn is_trusted_research_domain(name: &str) -> bool {
        let research_tlds = vec![
            "ieee.org",
            "acm.org",
            "nature.com",
            "sciencemag.org",
            "cell.com",
            "arxiv.org",
            "springer.com",
            "wiley.com",
            "elsevier.com",
            "researchgate.net",
            "cern.ch",
            "icml.cc",
            "neurips.cc",
            "openai.com",
            "deepmind.google",
            "ethz.ch",
            "max-planck.de",
            "cnrs.fr",
            "csiro.au",
            "inria.fr",
            "res.in",
        ];

        if name.ends_with(".res.in")
        {
            return true;
        }

        research_tlds.iter().any(|ld|
            name.ends_with(ld)
        )
    }

    pub fn is_trusted_healthcare_domain(name: &str) -> bool {
        if name.ends_with(".med")
            || name.ends_with(".pharmacy")
        {
            true
        }
        else
        {
            false
        }
    }

    pub fn is_trusted_financial_domain(name: &str) -> bool {
        if name.ends_with(".bank")
            || name.ends_with(".insurance")
            || name.ends_with(".cpa")
            || name.ends_with(".bank.in")
        {
            true
        }
        else
        {
            false
        }
    }

    pub fn is_trusted_law_domain(name: &str) -> bool {
        if name.ends_with(".law")
        {
            true
        }
        else
        {
            false
        }
    }
}