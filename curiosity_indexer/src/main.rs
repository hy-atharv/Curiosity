mod services;
mod utils;
mod dto;

use std::sync::Arc;
use dashmap::DashMap;
use rustls::crypto::{CryptoProvider, ring};
use actix_web::{web, App, HttpResponse, HttpServer, Responder};
use tokio::sync::broadcast;
use crate::dto::crawler_dto::CrawlState;

#[derive(Clone)]
struct AppState{
    pub queries_state: Arc<DashMap<String, Vec<f32>>>,
    crawl_state_events_tx: broadcast::Sender<CrawlState>,
}

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    CryptoProvider::install_default(ring::default_provider())
        .expect("Failed to install rustls crypto provider");

    let (crawl_state_tx, crawl_state_rx) = broadcast::channel::<CrawlState>(100);

    let state = AppState{
        queries_state: Arc::new(DashMap::new()),
        crawl_state_events_tx: crawl_state_tx
    };

    HttpServer::new(move || {
        App::new()
            .app_data(web::Data::new(state.clone()))
            .route("/info", web::get().to(backend_info))
            .service(services::index_results::index_results)
            .service(services::crawl_results::crawl_results)
    })
    .bind(("0.0.0.0", 8080))?
    .run()
    .await
}

async fn backend_info() -> impl Responder {
    HttpResponse::Ok().body("Curiosity Indexer is running...")
}